package com.systex.sysgateii.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.sysgateii.comm.Constants;
import com.systex.sysgateii.listener.ActorStatusListener;
import com.systex.sysgateii.ratesvr.telegram.S004;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
//import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.io.FileUtils;

@Sharable // 因為通道只有一組 handler instance 只有一個，所以可以 share
public class ClientConnection extends ChannelDuplexHandler implements Runnable {
	private static Logger log = LoggerFactory.getLogger(ClientConnection.class);
	public final static String KEY_NAME = "CAT";
	String clientId;

	// for ChannelDuplexHandler function
	ChannelHandlerContext currentContext;
	Channel clientChannel;
	private CountDownLatch readLatch;
	private String idleStateHandlerName = "idleStateHandler";
	//20200105
//	byte[] clientMessage;
	//----
	//20200105
	public ByteBuf clientMessageBuf = Unpooled.buffer(16384);
	//----
	Object readMutex = new Object();
	// end for ChannelDuplexHandler function

	private Bootstrap bootstrap = new Bootstrap();
	private final static AtomicBoolean isConnected = new AtomicBoolean(false);

	private InetSocketAddress rmtaddr = null;
	private InetSocketAddress localaddr = null;
	private Channel channel_;
	private Timer timer_;
	private List<String> brnoList = null;
	private List<String> wsnoList = null;
	private String verhbrno = "984";
	private String verhwsno = "80";
	private String getSeqStr = "";
	private String curMrkttm = "";
	private int bufferSize = Integer.parseInt(System.getProperty("bufferSize", Constants.DEF_CHANNEL_BUFFER_SIZE + ""));
//	private String frameDecoderName = "frameDecoder";
	private boolean S004Start = false;
	private S004 s004tele = null;
	private File seqNoFile;

	List<ActorStatusListener> actorStatusListeners = new ArrayList<ActorStatusListener>();

	public List<ActorStatusListener> getActorStatusListeners() {
		return actorStatusListeners;
	}

	public void setActorStatusListeners(List<ActorStatusListener> actorStatusListeners) {
		this.actorStatusListeners = actorStatusListeners;
	}

	public ClientConnection(String rmthost, int rmtport, String verhbrno, String verhwsno, List<String> brnolist, List<String> wsnolist, Timer timer) {
		this(new InetSocketAddress(rmthost, rmtport), null, verhbrno, verhwsno, brnolist, wsnolist, timer);
	}

	public ClientConnection(String rmthost, int rmtport, String localhost, int localport, String verhbrno, String verhwsno, List<String> brnolist,
			List<String> wsnolist, Timer timer) {
		this(new InetSocketAddress(rmthost, rmtport), new InetSocketAddress(localhost, localport), verhbrno, verhwsno, brnolist, wsnolist,
				timer);
	}

	public ClientConnection(ConcurrentHashMap<String, String> map, List<String> brnolist, List<String> wsnolist, Timer timer) {
		String rmthost = map.get("svrsubport.svrip");
		String localhost = map.get("svrsubport.localip");
		this.verhbrno = map.get("svrsubport.verhbrno");
		this.verhwsno = map.get("svrsubport.verhwsno");
		this.rmtaddr = new InetSocketAddress(rmthost, Integer.parseInt(map.get("svrsubport.svrport")));
		this.localaddr = new InetSocketAddress(localhost, Integer.parseInt(map.get("svrsubport.localport")));
		this.brnoList = brnolist;
		this.wsnoList = wsnolist;
		this.timer_ = timer;
	}

	public ClientConnection(InetSocketAddress _rmtaddr, InetSocketAddress _localaddr, String verhbrno, String verhwsno, List<String> brnolist,
			List<String> wsnolist, Timer timer) {
		this.rmtaddr = _rmtaddr;
		this.localaddr = _localaddr;
		this.verhbrno = verhbrno;
		this.verhwsno = verhwsno;
		this.brnoList = brnolist;
		this.wsnoList = wsnolist;
		this.timer_ = timer;
	}

	public void send(String msg) throws IOException {
		if (channel_ != null && channel_.isActive()) {
			ByteBuf buf = channel_.alloc().buffer().writeBytes(msg.getBytes());
			channel_.writeAndFlush(buf);
		} else {
			throw new IOException("Can't send message to inactive connection");
		}
	}

	public void sendBytes(byte[] msg) throws IOException {
		if (channel_ != null && channel_.isActive()) {
			ByteBuf buf = channel_.alloc().buffer().writeBytes(msg);
			channel_.writeAndFlush(buf);
		} else {
			throw new IOException("Can't send message to inactive connection");
		}
	}

	public void close() {
		try {
			channel_.close().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void doConnect(long _wait) {
		try {
			ChannelFuture f = bootstrap.connect(rmtaddr, localaddr);
			f.addListener(new ChannelFutureListener() {
				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if (!future.isSuccess()) {// if is not successful, reconnect
						future.channel().close();
						log.debug("seceduleConnect");
						//20200105
						clientMessageBuf.clear();
						//----
						try {
							Thread.sleep(_wait);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						bootstrap.connect(rmtaddr, localaddr).addListener(this);
					} else {// good, the connection is ok
						channel_ = future.channel();
						// add a listener to detect the connection lost
						addCloseDetectListener(channel_);
						connectionEstablished();
					}
				}

				private void addCloseDetectListener(Channel channel) {
					// if the channel connection is lost, the
					// ChannelFutureListener.operationComplete() will be called
					channel.closeFuture().addListener(new ChannelFutureListener() {
						@Override
						public void operationComplete(ChannelFuture future) throws Exception {
							connectionLost();
							scheduleConnect(3000);
						}

					});

				}
			});
		} catch (Exception ex) {
			scheduleConnect(1000);

		}
	}

	private void scheduleConnect(long millis) {
		timer_.schedule(new TimerTask() {
			@Override
			public void run() {
				doConnect(millis);
			}
		}, millis);
	}

	public void handleMessage(String msg) {
		log.debug("msg={}", msg);

	}

	public void connectionLost() {
		log.debug("connectionLost()");
	}

	public void connectionEstablished() {
		log.debug("connectionEstablished()");
		//20200105
		clientMessageBuf.clear();
		//----
	}

	@Override
	public void run() {
		bootstrap.group(new NioEventLoopGroup());
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_REUSEADDR, true);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.option(ChannelOption.SO_LINGER, 0);
		bootstrap.option(ChannelOption.SO_REUSEADDR, true);
		bootstrap.option(ChannelOption.TCP_NODELAY, true);
		bootstrap.option(ChannelOption.ALLOW_HALF_CLOSURE, false);
		bootstrap.option(ChannelOption.SO_RCVBUF, bufferSize);
		bootstrap.option(ChannelOption.SO_SNDBUF, bufferSize);
		bootstrap.option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(32768, 32768, 32768));
		seqNoFile = new File("SEQNO", "SEQNO_" + this.localaddr.getPort());
		log.debug("seqNoFile local=" + seqNoFile.getAbsolutePath());
		if (seqNoFile.exists() == false) {
			File parent = seqNoFile.getParentFile();
			if (parent.exists() == false) {
				parent.mkdirs();
			}
			try {
				seqNoFile.createNewFile();
				FileUtils.writeStringToFile(seqNoFile, "0", Charset.defaultCharset());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				ch.pipeline().addLast("log", new LoggingHandler(ClientConnection.class, LogLevel.INFO));
				ch.pipeline().addLast(getHandler("FAS"));
			}
		});
		scheduleConnect(1000);

	} // run

	private List<String> cnvS004toR0061(byte[] src) {
		List<String> rtnList = null;
		byte[] rtn = src;

		if (src != null && src.length > 47) {
			try {
				if (new String(src, 38, 4, CharsetUtil.UTF_8).equals("S004")) {
					// S004 telegram
					if (!this.S004Start) {
						this.S004Start = true;
						this.s004tele = new S004(brnoList.get(0), wsnoList.get(0));
					} else if (src[32] == (byte) '1') {
						this.S004Start = false;
					}
					log.debug("S004 {} telegram", src[32] == (byte) '0' ? "" : "last");
					rtn = new byte[src.length - 47];
					System.arraycopy(src, 47, rtn, 0, src.length - 47);
					this.s004tele.setData(rtn);
					log.debug("brno 0 ={}", brnoList.get(0));
					log.debug("wsno 0 ={}", wsnoList.get(0));
					log.debug("getMrktdt()={}", new String(this.s004tele.getMrktdt()));
					log.debug("getSysdt={}", new String(this.s004tele.getSysdt()));
					curMrkttm = new String(this.s004tele.getMrkttm());
					log.debug("getMrkttm={}", curMrkttm);
					log.debug("getType={}", new String(this.s004tele.getType()));
					if (this.S004Start == false) {
						log.debug("RateRecList={}", this.s004tele.getRateRecList().size());
						rtnList = this.s004tele.getRateRecList();
						this.s004tele = null;
					}
				}
			} catch (Exception e) {
				log.debug("not S004 telegram");
			}
		}
		return rtnList;
	}
	
	//20200105
	private int fromByteArray(byte[] bytes) {
	    int r = 0;
	    for (byte b: bytes)
	        r =  (r * 100) + ((((b >> 4)& 0xf) * 10 + (b & 0xf)));
	    return r;
	}
	//----
	private void HostS004SndHost(int seq, String brno, String wsno, String mrkttm) {
		String S004TITAStr = String.format(
				"\u000f\u000f\u000f\u0000\u0001d\u0001%03d\u000f\u000f%03d%02d0\u0000006100000000000000000FU0700C8400000000000000000000000000000000000000000000000014000000000000000000000001000000000000000000000%03d000000001%4s?\u0004",
				seq, Integer.parseInt(brno), Integer.parseInt(wsno), Integer.parseInt(brno), mrkttm);
		byte[] S004TITA = new byte[S004TITAStr.length()];
		System.arraycopy(S004TITAStr.getBytes(), 0, S004TITA, 0, S004TITAStr.getBytes().length);
		try {
			sendBytes(S004TITA);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// for ChannelDuplexHandler function

	public ChannelHandler getHandler(String _id) {
		clientId = _id;
		return this;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.debug(clientId + "channel active");
		this.currentContext = ctx;
		this.clientChannel = this.currentContext.channel();
		publishActiveEvent();
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.debug(clientId + " channelInactive");
		publishInactiveEvent();
		this.clientChannel = null;
		//20200105
		this.clientMessageBuf.clear();
		//----
		super.channelInactive(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		log.debug(clientId + " channelRead");
		//20200105
		byte [] telmbyteary = null;
		//----
		try {
			if (msg instanceof ByteBuf) {
				ByteBuf buf = (ByteBuf) msg;
				log.debug("buf capacity=" + buf.capacity());
				if (buf.isReadable()) {
					log.debug("readable");
					int size = buf.readableBytes();
					log.debug("readableBytes={} barray={}", size, buf.hasArray());
					if (buf.isReadable() && !buf.hasArray()) {
						// it is long raw telegram
						//20200105
						log.debug("readableBytes={} barray={}", buf.readableBytes(), buf.hasArray());
						if (clientMessageBuf.readerIndex() > (clientMessageBuf.capacity() / 2)) {
							clientMessageBuf.discardReadBytes();
							log.debug("adjustment clientMessageBuf readerindex ={}" + clientMessageBuf.readableBytes());
						}
						//----
						synchronized (this.readMutex) {
							//20200105
							size = buf.readableBytes();
							clientMessageBuf.writeBytes(buf);
							log.debug("clientMessageBuf.readableBytes={}",clientMessageBuf.readableBytes());
							while (clientMessageBuf.readableBytes() >= 12) {
								byte[] lenbary = new byte[3];
								clientMessageBuf.getBytes(clientMessageBuf.readerIndex() + 3, lenbary);
								log.debug("clientMessageBuf.readableBytes={} size={}",clientMessageBuf.readableBytes(), fromByteArray(lenbary));
								if ((size = fromByteArray(lenbary)) > 0 && size <= clientMessageBuf.readableBytes()) {
									telmbyteary = new byte[size];
									clientMessageBuf.readBytes(telmbyteary);
									log.debug("read {} byte(s) from clientMessageBuf after {}", size, clientMessageBuf.readableBytes());
									getSeqStr = new String(telmbyteary, 7, 3);
									FileUtils.writeStringToFile(seqNoFile, getSeqStr, Charset.defaultCharset());
									List<String> rlist = cnvS004toR0061(telmbyteary);
									if (rlist != null && rlist.size() > 0) {
										for (String l : rlist) {
											telmbyteary = l.getBytes();
											buf = channel_.alloc().buffer().writeBytes(telmbyteary);
											publishactorSendmessage(clientId, buf);
										}
										try {
											int seqno = Integer.parseInt(
													FileUtils.readFileToString(seqNoFile, Charset.defaultCharset())) + 1;
											if (seqno > 999) {
												seqno = 0;
											}
											HostS004SndHost(seqno, verhbrno, verhwsno, curMrkttm);
											FileUtils.writeStringToFile(seqNoFile, Integer.toString(seqno), Charset.defaultCharset());
										} catch (Exception e) {
											log.warn(e.getMessage());
										}
									}

								} else
									break;
								
							}
						}
					}
				}
			} else // if
				log.warn("not ByteBuf");
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.toString());
		} finally {
			ReferenceCountUtil.release(msg);
			// 若是有配置等待鎖，則解鎖
			if (readLatch != null) {
				readLatch.countDown();
			}
		}
	}

	/**
	 * it's depends also on ChannelOption.MAX_MESSAGES_PER_READ which is 16 by
	 * default 當每一部份的訊息被讀取後會被呼叫 例如 buffer 中有 32 bytes，此功能會被呼叫 2 次
	 * .option(ChannelOption.MAX_MESSAGES_PER_READ, Integer.MAX_VALUE)
	 * 
	 * @throws Exception
	 */
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		log.debug(clientId + " channelReadComplete");
		super.channelReadComplete(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		log.debug(clientId + " exceptionCaught=" + cause.getMessage());
		publishInactiveEvent();
		//20200105
		this.clientMessageBuf.clear();
		//----
		ctx.close();
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		log.debug(clientId + " userEventTriggered=" + evt.toString());
		if (evt instanceof IdleStateEvent) {
			if (clientChannel.pipeline().get(idleStateHandlerName) != null) {
				log.debug("unload idle state handler");
				clientChannel.pipeline().remove(idleStateHandlerName);
			}

			IdleStateEvent e = (IdleStateEvent) evt;
			if (e.state() == IdleState.READER_IDLE) {
				log.debug(clientId + " READER_IDLE");
			} else if (e.state() == IdleState.WRITER_IDLE) {
				log.debug(clientId + " WRITER_IDLE");
			} else if (e.state() == IdleState.ALL_IDLE) {
				log.debug(clientId + " ALL_IDLE");
			}

			// 通知即將斷線
			publishInactiveEvent();
			ctx.close();
		}
	}

	public synchronized void addActorStatusListener(ActorStatusListener listener) {
		log.debug(clientId + " actor status listener add");
		actorStatusListeners.add(listener);
	}

	public synchronized void removeActorStatusListener(ActorStatusListener listener) {
		log.debug(clientId + " actor status listener remove");
		actorStatusListeners.remove(listener);
	}

	public void publishShutdownEvent() {
		log.debug(clientId + " publish shutdown event to listener");
		log.debug("-publish end-");
	}

	public void publishActiveEvent() {
		log.debug(clientId + " publish active event to listener");
		this.isConnected.set(true);
		log.debug("-publish end-");
	}

	public void publishInactiveEvent() {
		log.debug(clientId + " publish Inactive event to listener");
		this.isConnected.set(false);
		log.debug("-publish end-");
	}

	public void publishactorSendmessage(String actorId, Object eventObj) {
		log.debug(actorId + " publish message to listener");
		for (ActorStatusListener listener : actorStatusListeners) {
			listener.actorSendmessage(clientId, eventObj);
		}

		log.debug("-publish end-");
	}

	// end for ChannelDuplexHandler function

	public static void sleep(int t) {
		try {
			Thread.sleep(t * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String... args) {
		List<String> brnos = new ArrayList<String>();
		brnos.add("984");
		List<String> wsnos = new ArrayList<String>();
		wsnos.add("9800");
		ClientConnection conn = new ClientConnection("127.0.0.1", 34567, "984", "80", brnos, wsnos, new Timer());

		for (;;) {
			try {
				Thread.sleep(100);
			} catch (Exception ex) {
			}
		}
	}

}
