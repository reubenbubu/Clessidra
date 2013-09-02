package com.ccbill.clessidra.aspect;

import java.util.UUID;

/**
 * Provides thread safe UUID generation
 * 
 * @author reubena
 * 
 */
public class UUIDGenerator {

	/**
	 * A thread safe method that generates a UUID
	 * 
	 * @return The generated UUID
	 */
	public static synchronized UUID generateUUID() {
		return UUID.randomUUID();
	}
}
