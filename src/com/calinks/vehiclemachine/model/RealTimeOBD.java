package com.calinks.vehiclemachine.model;

import java.io.Serializable;
/**
 * 实时数据
 * @author Administrator
 *
 */
public class RealTimeOBD implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//˲ʱ�ͺ�
	public String oilConsum;
	
	//����ת��
	public String rotateSpeed;
	
	//����
	public String carSpeed;
	
	//��ȴҺ�¶�
	public String coolantTemp;
	
	//acc����
	public boolean isAccOpen;
	
	//��������Ϣ
	public FaultInfo faultInfo;
	
//	//�����г̿�ʼʱ��
//	public long travelStartTime;
//	
//	//�����г̽���ʱ��
//	public long travelEndTime;
//	
//	//�����г̹���
//	public String travelMeters;
//	
//	//�����г̺���
//	public String travelOilConsum;
}
