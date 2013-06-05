package com.calinks.vehiclemachine.model;

import java.io.Serializable;

public class PortData implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public  String cmd;
	public byte[] parameters;
	public int length;
}
