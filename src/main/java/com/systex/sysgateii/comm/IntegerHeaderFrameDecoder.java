package com.systex.sysgateii.comm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

public class IntegerHeaderFrameDecoder extends ReplayingDecoder<IntegerHeaderFrameDecoder.DecoderState> {
	private static Logger log = LoggerFactory.getLogger(IntegerHeaderFrameDecoder.class);

	public static enum DecoderState {
		READ_LENGTH, READ_CONTENT;
	}

	int length;

	public IntegerHeaderFrameDecoder() {
		// Set the initial state.
		super(DecoderState.READ_LENGTH);
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf buf, List<Object> out) throws Exception {
		switch (state()) {
		case READ_LENGTH:
			length = buf.readInt();
			log.debug("length=" + length);
			checkpoint(DecoderState.READ_CONTENT);
		case READ_CONTENT:
			// 前 4 個bytes 是電文長度，包含長度自已
			ByteBuf frame = buf.readBytes(length - 4);
			checkpoint(DecoderState.READ_LENGTH);

			// log.debug("total readable bytes=" + super.actualReadableBytes());
			// out.add(buf.readBytes(super.actualReadableBytes()));
			log.debug("total readable bytes=" + buf.readableBytes());
			out.add(frame);
			break;
		default:
			throw new Error("Shouldn't reach here.");
		} // switch
	}
}
