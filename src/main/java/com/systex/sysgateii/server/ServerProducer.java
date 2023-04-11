package com.systex.sysgateii.server;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import com.systex.sysgateii.comm.Constants;
//import com.systex.sysgateii.comm.TxIdleStateHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;  //20220709 MataudairaSyuMe
import io.netty.util.CharsetUtil;

import com.systex.sysgateii.util.StrUtil;
import com.systex.sysgateii.listener.ActorStatusListener;
import com.systex.sysgateii.ratesvr.dao.GwCfgDao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.time.LocalDate;
import java.time.chrono.MinguoDate;
import java.time.format.DateTimeFormatter;

/**
 * <pre>
 * 因為 write 端目前沒有過濾測試電文，也就是測試電文也算是 none idle，所以不太會 timeout
 * 必需依賴外部來控制是否 timeout 結束
 * 使用 ipv4 請於命令列下指令：
 *   -Djava.net.preferIPv4Stack=true
 * 或使用 
 *   System.setProperty("java.net.preferIPv4Stack" , "true");
 * </pre>
 * 
 * @author Matsudaira Syume
 *
 */

@Sharable // 因為通道只有一組 handler instance 只有一個，所以可以 share
public class ServerProducer extends ChannelDuplexHandler // ChannelInboundHandlerAdapter
		implements Runnable, ActorStatusListener {
	private static Logger log = LoggerFactory.getLogger(ServerProducer.class);
	private final static AtomicBoolean isConnected = new AtomicBoolean(false);

	// 建立儲存 sync check 值的屬性
	// private final AttributeKey<Byte[]> syncCheck =
	// AttributeKey.valueOf(Constants.LABEL_KEY_SYNC);
	EventLoopGroup bossGroup = new NioEventLoopGroup(1);
	EventLoopGroup workerGroup = new NioEventLoopGroup(1);

	int bufferSize;
	int port;
	String bindAddress;
	String serverId;
	int keepAlive;
	int idleTimeout;
	ChannelFuture listenFuture;
	ServerBootstrap bootStrap;
	Channel listenChannel;
	private IdleStateHandler idleStateHandler;  //20220712 MatsjudairaSyuMe
	ChannelHandlerContext currentContext;
	private String rtnHdrfmt = "TD%03d%02d00000%04d0000";
	private String rtnTimefmt = "TD%03d%02d0001T02460001000000000000000000%15s000%03d%02d000TD0010000%04d1                    0                    000000000%8s00000002%8s%8s%8s%8s%8s%8s010101%8s%8s%8s%8s%8s%8s";
//20220707 MatsudairaSyuMe
	//	private String rtnC25 = "TD981410011E0164    3E013                                                                                                         [SCTL]                            ";
	private String rtnC25 = "TD%02d1410011E0164    3E013                                                                                                         [SCTL]                            ";
	//20220707 MatsudairaSyuMe
	private String currentBRWS = "";
	//----
	// allowed client ip list
	List<String> ipList = new ArrayList<String>();
	//2020015
	List<String> brnoList = new ArrayList<String>();
	ConcurrentHashMap<String, List<String>> brnoaddrGrp = new ConcurrentHashMap<String, List<String>>();
	//----
	List<ChannelHandlerContext> sessionList = new ArrayList<ChannelHandlerContext>();
	public ChannelHandlerContext getCurrentContext() {
		return currentContext;
	}

	public void setCurrentContext(ChannelHandlerContext currentContext) {
		this.currentContext = currentContext;
	}

	public Channel getListenChannel() {
		return listenChannel;
	}

	public void setListenChannel(Channel listenChannel) {
		this.listenChannel = listenChannel;
	}

	public List<String> getIpList() {
		return ipList;
	}

	public void setIpList(List<String> IpList) {
		this.ipList = IpList;
	}
	
	//20200215
	public List<String> getBrnoList() {
		return brnoList;
	}

	public void setBrnoaddrGrp(ConcurrentHashMap<String, List<String>> brnoaddrGrp) {
		this.brnoaddrGrp = brnoaddrGrp;
	}
	public ConcurrentHashMap<String, List<String>> getBrnoaddrGrp() {
		return brnoaddrGrp;
	}

	public void setBrnoList(List<String> BrnoList) {
		this.brnoList = BrnoList;
	}
	//----
	
	public int getKeepAlive() {
		return keepAlive;
	}

	public void setKeepAlive(int keepAlive) {
		this.keepAlive = keepAlive;
	}

	public int getIdleTimeout() {
		return idleTimeout;
	}

	public void setIdleTimeout(int idleTimeout) {
		this.idleTimeout = idleTimeout;
	}

	public ServerBootstrap getBootStrap() {
		return bootStrap;
	}

	public void setBootStrap(ServerBootstrap bootStrap) {
		this.bootStrap = bootStrap;
	}

	public ServerProducer() {
		this.bufferSize = Integer.parseInt(System.getProperty("bufferSize", Constants.DEF_CHANNEL_BUFFER_SIZE + ""));
		this.port = 9998;
		this.bindAddress = System.getProperty("bind", Constants.DEF_SERVER_ADDRESS);
		this.serverId = "Producer";
		this.keepAlive = Integer.parseInt(System.getProperty("keepAlive", Constants.DEF_KEEP_ALIVE + ""));
		this.idleTimeout = Integer.parseInt(System.getProperty("idleTimeout", Constants.DEF_IDLE_TIMEOUT + ""));
	}

	public ServerProducer(String serverId, String bindAddress, int port, int bufferSize, int keepAlive,
			int idleTimeout) {
		this.serverId = serverId;
		this.bindAddress = bindAddress;
		this.port = port;
		this.bufferSize = bufferSize;
		this.keepAlive = keepAlive;
		this.idleTimeout = idleTimeout;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getBindAddress() {
		return bindAddress;
	}

	public void setBindAddress(String bindAddress) {
		this.bindAddress = bindAddress;
	}

	public ChannelFuture getListenFuture() {
		return listenFuture;
	}

	public void setListenFuture(ChannelFuture listenFuture) {
		this.listenFuture = listenFuture;
	}

	public ChannelHandler getHandler() {
		return this;
	}

	@Override
	public void run() {
		log.debug("Server run start.");

		try {
			bootStrap = new ServerBootstrap();
			bootStrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.childHandler(new ChannelInitializer<SocketChannel>() {
						@Override
						// The ChannelInitializer is a special handler that is purposed
						// to help a user configure a new Channel.
						public void initChannel(SocketChannel ch) throws Exception {
							// 若已有連線，就拒絕新的連線，以避免連線被惡意踢出
							// mark for not use currently 20190820
//							if (isConnected.get()) {
//								log.debug("already connected!");
//								ch.close();
//								return;
//							}
							// ---------------- mark 20190820

							// remote address /0:0:0:0:0:0:0:1:10335
							String socketChannel = ch.toString();
							int start = socketChannel.lastIndexOf("R:/") + 3;
							int end = socketChannel.lastIndexOf(":");
							String clientIp = socketChannel.substring(start, end);
//							String clientIp = ch.remoteAddress().getAddress().getHostAddress();
							log.debug("client from " + clientIp);
							if (!isValidClientIp(clientIp)) {
								log.debug("Invalid client ip =" + clientIp);
								ch.close();
								return;
							}
							//20230411 MatsudairaSyuMe check if this IP has any previous connection?
							if (isAlreadyConnectedIp(clientIp))
								log.debug("close previous connected session from [{}]",clientIp);							
							//20230411 MatsudairaSyuMe

							// Do not call await() inside ChannelHandler
							//ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(keepAlive, 0,
							//idleTimeout));
//							idleStateHandler = new TxIdleStateHandler(0, keepAlive, idleTimeout);
//							ch.pipeline().addLast("idleStateHandler", idleStateHandler);
							ch.pipeline().addLast("idleStateHandler", new IdleStateHandler(0, 0, 120, TimeUnit.SECONDS));
							ch.pipeline().addLast("log", new LoggingHandler(ServerProducer.class, LogLevel.INFO)); // 測試用
							ch.pipeline().addLast(getHandler());

						}
					}).option(ChannelOption.SO_BACKLOG, 128).option(ChannelOption.SO_REUSEADDR, true)
					.childOption(ChannelOption.SO_KEEPALIVE, true).childOption(ChannelOption.SO_LINGER, 0)
					.childOption(ChannelOption.SO_REUSEADDR, true).childOption(ChannelOption.SO_RCVBUF, bufferSize)
				.childOption(ChannelOption.SO_SNDBUF, bufferSize).childOption(ChannelOption.TCP_NODELAY, true)
//20220709					.childOption(ChannelOption.SO_SNDBUF, bufferSize).childOption(ChannelOption.TCP_NODELAY, false)
					.childOption(ChannelOption.ALLOW_HALF_CLOSURE, false)
					/**/
					.childOption(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(32768, 32768, 32768));
			/*
			 * Matsudaira Syume for FixedRecvByteBufAllocator not split telegram
			 */
			//20200215 for receive all interface connection
			// Bind and start to accept incoming connections.
			// Waits for this future until it is done, and rethrows
			// the cause of the failure if this future failed.
			// Bind -> wait for channel close -> channel close future -> wait for future
			// close
			// ChannelFuture f = b.bind(bindAddr,
			// port).sync().channel().closeFuture().sync();
			log.debug("bind to " + bindAddress + " port=" + port + " with buffer size=" + bufferSize);
			if (bindAddress.trim().equals("*"))
				listenFuture = bootStrap.bind(port).sync().channel().closeFuture().sync();
			else
				listenFuture = bootStrap.bind(bindAddress, port).sync().channel().closeFuture().sync();
			//--------
			// add to channel group
			// f.channel();
			// allChannels.add(xxx);
			listenChannel = listenFuture.channel();

			listenFuture.sync().channel().closeFuture().sync();

			// ... Wait until the shutdown signal reception ...

			// Close the serverChannel and then all accepted connections.
			// allChannels.close().awaitUninterruptibly();

		} catch (Exception e) {
			e.printStackTrace();
			log.error(e.toString());
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}

	} // run

	public ChannelFuture writeMessage(String msg, Charset chs) {
		log.debug(serverId + " write string message");
		ByteBuf req = Unpooled.wrappedBuffer(msg.getBytes(chs));
		log.debug("msg send");
		return currentContext.writeAndFlush(req);
	}

	public ChannelFuture writeMessageWithContext(ChannelHandlerContext ctx, String msg, Charset chs) {
		ByteBuf req = Unpooled.wrappedBuffer(msg.getBytes(chs));
		log.debug("assign ctx {} {} msg length={} send end", ctx, serverId, msg.length());
		return ctx.writeAndFlush(req);
	}

	public ChannelFuture writeMessage(byte[] msg) {
		log.debug("serverId=[{}] write byte message {}", serverId, msg.length);
		ByteBuf req = Unpooled.wrappedBuffer(msg);
		log.debug("msg send");

		// 測試用
		return currentContext.writeAndFlush(req);
	}

	/**
	 * 以白名單方式檢查 client ip 是否合法<br/>
	 * 當白名單無內容，表示不檢查
	 * 
	 * @param ip client ip
	 * @return true=合法, false=不合法
	 */
	public boolean isValidClientIp(String ip) {
		log.debug("lpList {} check {}", ipList, ip);
		if (ipList != null && ipList.size() > 0 && StrUtil.isNotEmpty(ip)) {
			for (String s : ipList) {
//				log.debug("lpList{} ==> check {}", s, ip);
				if (StrUtil.isNotEmpty(s) && ip.startsWith(s)) {
					log.debug("lpList{} ==> check {} inlist ", s, ip);
					return true;
				}
			}
		}

		if (ipList != null && ipList.size() == 0) {
			log.debug("isValidClientIp no ip list");
			return true;
		}

		return false;
	}

	/**
	 * 用來移除 idle state handler，如果有必要的話
	 */
	public void removeIdleStateHandler() {
		currentContext.pipeline().remove(idleStateHandler);
	}

	private String convertTWDate(String AD, String beforeFormat, String afterFormat) {// 轉年月格式
		if (AD == null)
			return "";
		SimpleDateFormat df4 = new SimpleDateFormat(beforeFormat);
		SimpleDateFormat df2 = new SimpleDateFormat(afterFormat);
		Calendar cal = Calendar.getInstance();
		try {
			cal.setTime(df4.parse(AD));
			if (cal.get(Calendar.YEAR) > 1492)
				cal.add(Calendar.YEAR, -1911);
			else
				cal.add(Calendar.YEAR, +1911);
			return df2.format(cal.getTime());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private String convertcurTWDate(String beforeFormat, String afterFormat) {// 轉年月格式
		SimpleDateFormat df2 = new SimpleDateFormat(afterFormat);
		Calendar cal = Calendar.getInstance();
		try {
			Date date = new Date();
			cal.setTime(date);
			if (cal.get(Calendar.YEAR) > 1492)
				cal.add(Calendar.YEAR, -1911);
			else
				cal.add(Calendar.YEAR, +1911);
			return df2.format(cal.getTime());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private String convertaddDayTWDate(String beforeFormat, String afterFormat, int addDay) {// 轉年月格式
		SimpleDateFormat df2 = new SimpleDateFormat(afterFormat);
		Calendar cal = Calendar.getInstance();
		try {
			Date date = new Date();
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_YEAR, addDay);
			if (cal.get(Calendar.YEAR) > 1492)
				cal.add(Calendar.YEAR, -1911);
			else
				cal.add(Calendar.YEAR, +1911);
			return df2.format(cal.getTime());
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private String convertlastDayofPreviousMonTWDate() {// 轉年月格式
		String gets = LocalDate.now().withDayOfMonth(1).minusDays(1).toString();
		return convertTWDate(gets, "yyyy-MM-dd", "0yyyMMdd");
	}

	private String convertlastDayofNextMonTWDate() {// 轉年月格式
		String gets = LocalDate.now().plusMonths(1).withDayOfMonth(1).minusDays(1).toString();
		return convertTWDate(gets, "yyyy-MM-dd", "0yyyMMdd");
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		log.debug(serverId + " channelActive");
//		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
		String socketChannel = ctx.channel().toString();
		int start = socketChannel.lastIndexOf("R:/") + 3;
		int end = socketChannel.lastIndexOf(":");
		String clientIP = socketChannel.substring(start, end);
//        String clientIP = insocket.getAddress().getHostAddress();
		if(clientIP !=null) {
			try {
				GwCfgDao GwCfgDao = new GwCfgDao();
				GwCfgDao.UPDATETB_AUDEVSTS(clientIP, "2");
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		isConnected.set(true);
		// 記錄目前的 ChannelHandlerContext
		// ------
		//202207011 MatsudairaSyuMe
		this.currentBRWS = this.brnoList.get(this.ipList.indexOf(clientIP));
//		this.idleStateHandler = new IdleStateHandler(120, 0, 0, TimeUnit.SECONDS);
//		ctx.pipeline().addLast("idleStateHandler", idleStateHandler);
		log.info("curentBRWS=[{}]", this.currentBRWS);
		//----
		sessionList.add(ctx);
		log.debug(serverId + " channelActive sessionList size={} ", sessionList.size());
		// -----
		currentContext = ctx;
		publishActiveEvent();
		super.channelActive(ctx);
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		log.debug(serverId + " channelInactive");
		// 20191005
//		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
		String socketChannel = ctx.channel().toString();
		int start = socketChannel.lastIndexOf("R:/") + 3;
		int end = socketChannel.lastIndexOf(":");
		String clientIP = socketChannel.substring(start, end);
//		String clientIP = insocket.getAddress().getHostAddress();
		sessionList.remove(ctx);
		log.debug(serverId + " channelInActive sessionList size={} ", sessionList.size());
        if(clientIP !=null) {
			try {
				GwCfgDao GwCfgDao = new GwCfgDao();
				GwCfgDao.UPDATETB_AUDEVSTS(clientIP, "1"); //20220718 MAtsudairaSyuMe change inactive to 1
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		// 20191005
		publishInactiveEvent();
		isConnected.set(false);
		super.channelInactive(ctx);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		//20220707 MatsudairaSyuMe
		//log.debug(serverId + " channelRead");
		int brno = 984;
		int wrkno = 98;
		if (this.currentBRWS.trim().length() > 0) {
			brno = Integer.valueOf(this.currentBRWS.substring(0, 3));
			wrkno = Integer.valueOf(this.currentBRWS.substring(5, 7));
		}
		log.debug("[{}] channelRead brno=[{}] wrkno=[{}]", serverId, brno, wrkno);
		//--
		try {
			if (msg instanceof ByteBuf) {
				// UnpooledUnsafeDirectByteBuf bf = (UnpooledUnsafeDirectByteBuf) msg;
				ByteBuf buf = (ByteBuf) msg;
				if (buf.isReadable()) {
					log.debug("{}: capacity={} readableBytes={} barray={} nio={}", serverId, buf.capacity(),
							buf.readableBytes(), buf.hasArray(), buf.nioBufferCount());
					byte[] req = new byte[buf.readableBytes()];
					buf.readBytes(req);
					String body = new String(req, CharsetUtil.UTF_8).substring(0, req.length);
					log.debug("server receive order : " + body);
					if (body.equalsIgnoreCase("TD000000000000200000")) {
						writeMessage(String.format(rtnHdrfmt, brno, wrkno, 20), CharsetUtil.UTF_8);
					} else if (body.startsWith("CU") && body.endsWith("STATION OPEN00")) {
						writeMessage(String
								.format(rtnTimefmt, brno, wrkno, convertcurTWDate("西元 yyyy 年 MM 月 dd 日", "0yyyMMddHHmm0ss"),
										brno, wrkno, 1906, convertcurTWDate("西元 yyyy 年 MM 月 dd 日", "0yyyMMdd"),
										convertaddDayTWDate("西元 yyyy 年 MM 月 dd 日", "0yyyMMdd", 1),
										convertaddDayTWDate("西元 yyyy 年 MM 月 dd 日", "0yyyMMdd", 2),
										convertaddDayTWDate("西元 yyyy 年 MM 月 dd 日", "0yyyMMdd", -1),
										convertlastDayofPreviousMonTWDate(), convertlastDayofNextMonTWDate(),
										convertlastDayofNextMonTWDate(),
										convertaddDayTWDate("西元 yyyy 年 MM 月 dd 日", "0yyyMMdd", 3),
										convertaddDayTWDate("西元 yyyy 年 MM 月 dd 日", "0yyyMMdd", 4),
										convertaddDayTWDate("西元 yyyy 年 MM 月 dd 日", "0yyyMMdd", 5),
										convertaddDayTWDate("西元 yyyy 年 MM 月 dd 日", "0yyyMMdd", -2),
										convertaddDayTWDate("西元 yyyy 年 MM 月 dd 日", "0yyyMMdd", -3),
										convertaddDayTWDate("西元 yyyy 年 MM 月 dd 日", "0yyyMMdd", -4))
								.getBytes());
					} else if (body.contains("CU700C25")) {
						//20220707 MAtsudairaSyuMe
//						writeMessage(rtnC25.getBytes());
						writeMessage(String.format(rtnC25, wrkno).getBytes());
					}
				} else {
					log.debug("{}: capacity={} readableBytes={} barray={} nio={} can't be read", serverId,
							buf.capacity(), buf.readableBytes(), buf.hasArray(), buf.nioBufferCount());
				}
			} else {
				log.error("{}: not ByteBuf message", serverId);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.error(serverId + " " + e);
		}

		String body = (String) msg.toString();
		log.debug(serverId + ":" + body);

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		log.debug(serverId + " exceptionCaught=" + cause.getMessage());
//		InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
		String socketChannel = ctx.channel().toString();
		int start = socketChannel.lastIndexOf("R:/") + 3;
		int end = socketChannel.lastIndexOf(":");
		String clientIP = socketChannel.substring(start, end);
//        String clientIP = insocket.getAddress().getHostAddress();
        if(clientIP !=null) {
			try {
				GwCfgDao GwCfgDao = new GwCfgDao();
				GwCfgDao.UPDATETB_AUDEVSTS(clientIP, "1"); //20220718 MAtsudairaSyuMe change inactive to 1
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		publishInactiveEvent();
		ctx.close();
		// super.exceptionCaught(ctx, cause);
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		//20220718 MatsudairaSyuuMe
		String socketChannel = ctx.channel().toString();
		int start = socketChannel.lastIndexOf("R:/") + 3;
		int end = socketChannel.lastIndexOf(":");
		String clientIP = socketChannel.substring(start, end);
		String triggeredBRWS = this.brnoList.get(this.ipList.indexOf(clientIP));
		log.info("serverid=[{}] triggeredBRWS=[{}] userEventTriggered", serverId, triggeredBRWS);
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent e = (IdleStateEvent) evt;
			if (e.state() == IdleState.READER_IDLE) {
				log.debug(serverId + " READER_IDLE");
			} else if (e.state() == IdleState.WRITER_IDLE) {
				log.debug(serverId + " WRITER_IDLE");
			} else if (e.state() == IdleState.ALL_IDLE) {
				log.debug(serverId + " ALL_IDLE");
			}
			int brno = 984;
			int wrkno = 98;
			if (triggeredBRWS.length() > 0) {
				brno = Integer.valueOf(triggeredBRWS.substring(0, 3));
				wrkno = Integer.valueOf(triggeredBRWS.substring(5, 7));
			}
			String sndStr = null;
			sndStr = String
					.format(rtnTimefmt, brno, wrkno, convertcurTWDate("西元 yyyy 年 MM 月 dd 日", "0yyyMMddHHmm0ss"),
							brno, wrkno, 1906, convertcurTWDate("西元 yyyy 年 MM 月 dd 日", "0yyyMMdd"),
							convertaddDayTWDate("西元 yyyy 年 MM 月 dd 日", "0yyyMMdd", 1),
							convertaddDayTWDate("西元 yyyy 年 MM 月 dd 日", "0yyyMMdd", 2),
							convertaddDayTWDate("西元 yyyy 年 MM 月 dd 日", "0yyyMMdd", -1),
							convertlastDayofPreviousMonTWDate(), convertlastDayofNextMonTWDate(),
							convertlastDayofNextMonTWDate(),
							convertaddDayTWDate("西元 yyyy 年 MM 月 dd 日", "0yyyMMdd", 3),
							convertaddDayTWDate("西元 yyyy 年 MM 月 dd 日", "0yyyMMdd", 4),
							convertaddDayTWDate("西元 yyyy 年 MM 月 dd 日", "0yyyMMdd", 5),
							convertaddDayTWDate("西元 yyyy 年 MM 月 dd 日", "0yyyMMdd", -2),
							convertaddDayTWDate("西元 yyyy 年 MM 月 dd 日", "0yyyMMdd", -3),
							convertaddDayTWDate("西元 yyyy 年 MM 月 dd 日", "0yyyMMdd", -4));
			log.debug("serverId=[{}] userEventTriggered [{}]", serverId, sndStr);
			writeMessageWithContext(ctx, sndStr, CharsetUtil.UTF_8); //20220718 writeMessage change to use writeMessageWithContext
			//----
			//20230406 update the TB_AUDEVSTS status for connect status
			if(clientIP !=null) {
				try {
					GwCfgDao GwCfgDao = new GwCfgDao();
					GwCfgDao.UPDATETB_AUDEVSTS(clientIP, "2");
				} catch (Throwable e1) {
					e1.printStackTrace();
				}
			}
			//20230406 end
			publishInactiveEvent();
			//ctx.close();
		}
	}

	/**
	 * 接受 ClientConnectionHandler shutdown event
	 */
	@Override
	public void actorShutdown(String actorId) {
		log.debug(actorId + " actor shutdown event");
		bossGroup.shutdownGracefully();
		workerGroup.shutdownGracefully();
	}

	/**
	 * 接受 ClientConnectionHandler 啟動 event
	 */
	@Override
	public void actorActive(String actorId) {
	}

	/**
	 * 接受 ClientConnectionHandler channel 關閉 event
	 */
	@Override
	public void actorInactive(String actorId) {
		log.debug(actorId + " actor Inactive event");
		if (currentContext != null) {
			currentContext.close();
		}
	}

	/**
	 * 接受 ClientConnectionHandler channel 傳送訊息 event
	 */

	@Override
	public void actorSendmessage(String actorId, Object eventObj,List<String> targetaddr) {
		//20200215
		log.debug("actorSendmessage actorId {} sessionList size={} ", actorId, sessionList.size());
		log.debug("actorSendmessage brnoaddrGrp={} ", this.brnoaddrGrp);
		if (eventObj instanceof ByteBuf) {
			ByteBuf result = (ByteBuf) eventObj;
			byte[] result1 = new byte[result.readableBytes()];
			result.readBytes(result1);
			//20200215
			String rmtaddr = "";
			
//				targetaddr = this.brnoaddrGrp.get(actorId);
			//----
			for (ChannelHandlerContext curctx : sessionList) {
//				rmtaddr = ((InetSocketAddress) curctx.channel().remoteAddress()).getAddress().getHostAddress().trim();
				String socketChannel = curctx.channel().toString();
				int start = socketChannel.lastIndexOf("R:/") + 3;
				int end = socketChannel.lastIndexOf(":");
				rmtaddr = socketChannel.substring(start, end);
				log.debug("bordaddr={} brno {}", rmtaddr, actorId);
				if (curctx != null) {
					if (actorId.equals("999") || targetaddr != null && targetaddr.contains(rmtaddr)) {
						writeMessageWithContext(curctx, new String(result1), CharsetUtil.UTF_8);
						//20230406 update the TB_AUDEVSTS status for connect status
						if(rmtaddr !=null) {
							try {
								GwCfgDao GwCfgDao = new GwCfgDao();
								GwCfgDao.UPDATETB_AUDEVSTS(rmtaddr, "2");
							} catch (Throwable e) {
								e.printStackTrace();
							}
						}
						//20230406 end
					}
				}
			}
			result1 = null;
		}
	}

	public void publishShutdownEvent() {
		log.debug(serverId + " publish shutdown event to listener");
		log.debug("-publish end-");
	}

	public void publishActiveEvent() {
		log.debug(serverId + " publish active event to listener");
		log.debug("-publish end-");
	}

	public void publishInactiveEvent() {
		log.debug(serverId + " publish Inactive event to listener");
		log.debug("-publish end-");
	}


	//20230411 MatsudairaSyuMe close previous connection on same IP for making sure only one session of one ip
	/**
	 * 以檢查 client ip 是否仍然連接<br/>
	 *
	 * @param ip client ip
	 * @return true=連接, false=未曾連接
	 */
	public boolean isAlreadyConnectedIp(String ip) {
		log.debug("sessionList check {}", ip);
		if (sessionList != null && sessionList.size() > 0 && StrUtil.isNotEmpty(ip)) {
			for (ChannelHandlerContext curctx : sessionList) {
				String socketChannel = curctx.channel().toString();
				int start = socketChannel.lastIndexOf("R:/") + 3;
				int end = socketChannel.lastIndexOf(":");
				String rmtaddr = socketChannel.substring(start, end);
				log.debug("rmtaddr={} ip={}", rmtaddr, ip);
				if (StrUtil.isNotEmpty(rmtaddr) && ip.startsWith(rmtaddr)) {
					log.info("sessionList{} ==> check {} in sessionList close previous session", rmtaddr, ip);
					curctx.close();
					sessionList.remove(curctx);
					return true;
				}
			}
		}
		return false;
	}
	//20230411 end MatsudairaSyuMe close previous connection on same IP for making sure only one session of one ip

}
