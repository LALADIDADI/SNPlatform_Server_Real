package com.fan.boot.modules.momdrModule.Math;

import java.math.BigDecimal;

public class Combination
{
	public Combination()
	{

	}

	public BigDecimal C(int n, int k)
	{
		BigDecimal a = new BigDecimal("1");
		for(int i = 1 ; i <= k ; i++)
		{
			a = a.multiply(new BigDecimal(Integer.toString(i)));
		}
		BigDecimal b = new BigDecimal("1");
		for(int i = n - k + 1 ; i <= n ; i++)
		{
			b = b.multiply(new BigDecimal(Integer.toString(i)));
		}
		return b.divide(a);
	}
}
