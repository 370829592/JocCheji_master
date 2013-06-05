package com.calinks.vehiclemachine.model;

import java.io.Serializable;
import java.util.ArrayList;

public class FaultInfo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int length;
	public ArrayList<String> faultCodes = new ArrayList<String>();
}
