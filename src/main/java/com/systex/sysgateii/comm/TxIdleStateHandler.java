package com.systex.sysgateii.comm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.systex.sysgateii.comm.Constants;
import com.systex.sysgateii.util.DataConvert;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.handler.timeout.IdleStateHandler;

@Sharable
public class TxIdleStateHandler extends IdleStateHandler {
	private static Logger log = LoggerFactory.getLogger(TxIdleStateHandler.class);

	public TxIdleStateHandler(int readerIdleTimeSeconds, int writerIdleTimeSeconds, int allIdleTimeSeconds) {
		super(readerIdleTimeSeconds, writerIdleTimeSeconds, allIdleTimeSeconds);

	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//TODO:判斷 msg 是否為測試電文，若不是才呼叫父類別
		log.debug("msg=" + msg.toString());
//if (msg instanceof UnpooledUnsafeDirectByteBuf)
		if (msg instanceof ByteBuf) {
//UnpooledUnsafeDirectByteBuf bf = (UnpooledUnsafeDirectByteBuf) msg;
			ByteBuf bf = (ByteBuf) msg;
			log.debug("capacity=" + bf.capacity());
			if (bf.isReadable()) {
				log.debug("readable");
				int i = bf.readableBytes();
				log.debug("readableBytes=" + i);
				byte[] b = new byte[i];
//使用時必需要注意使用的方法是否會造成 readerIndex or writerIndex 的增加
//This method does not modify readerIndex or writerIndex of this buffer.
				bf.getBytes(0, b);

//讀取 header 中的 訊息類別及交易代碼
				byte[] p;
				String pcode;

				if (i < 14) {
					log.debug("Invalid telecom size");
					return;
				}

				p = new byte[8];
				System.arraycopy(b, 6, p, 0, p.length);
				pcode = new String(p);
				log.debug("PCODE=" + pcode);

				String s = DataConvert.bytesToHex(b);
				log.debug("DATA>>" + s);

//測試電文
				if (pcode.equals(Constants.DEF_PCODE_R)) {
					log.debug("get testing telecom " + pcode);
					return;
				}
			}
		} // if

		super.channelRead(ctx, msg);
	}
}
