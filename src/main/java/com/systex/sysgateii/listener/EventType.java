package com.systex.sysgateii.listener;

public enum EventType {
	ON_MESSAGE,
	ACTIVE,
	INACTIVE,
	SHUTDOWN,
	READ_IDLE,     //timeout
	WRITE_IDLE,
	ALL_IDLE,
	OP_SUCCESS,
	OP_FAIL,
	EXCEPTION
}
