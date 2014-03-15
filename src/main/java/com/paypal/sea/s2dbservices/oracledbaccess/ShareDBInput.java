package com.paypal.sea.s2dbservices.oracledbaccess;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "ShareDBInput")

public class ShareDBInput
{
	private String fromStageName;
	private String toStageName;

		public ShareDBInput()
		{
			fromStageName="";
			toStageName="";
		}
		public String getFromStageName()
		{
			return fromStageName;
		} 
		public void setFromStageName(String s1)
		{
			fromStageName = s1;
		} 
		public String getToStageName()
		{
			return toStageName;
		}
		public void setToStageName(String s2)
		{
			toStageName = s2;
		} 
}