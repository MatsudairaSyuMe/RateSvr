package com.systex.sysgateii.listener;

public interface ProducerStatusListener {

	public void producerActive(String serverId);

	public void producerInactive(String serverId);

	public void producerShutdown(String serverId);

}
