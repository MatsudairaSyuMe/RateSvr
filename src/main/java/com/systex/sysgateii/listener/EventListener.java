package com.systex.sysgateii.listener;

import com.systex.sysgateii.listener.EventType;

public interface EventListener {
	public void onEvent(String id, EventType evt, Object eventObj);

}
