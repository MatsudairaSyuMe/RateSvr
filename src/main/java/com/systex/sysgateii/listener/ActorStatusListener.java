package com.systex.sysgateii.listener;

import java.util.List;

import io.netty.channel.ChannelHandlerContext;

public interface ActorStatusListener {

	public void actorActive(String actorId);

	public void actorInactive(String actorId);

	public void actorShutdown(String actorId);

	public void actorSendmessage(String actorId, Object eventObj,List<String> targetaddr);
}
