package com.fan.boot.modules.momdrModule.Math;

public class CIvalue
{
	double upper = 0.0d;
	double lower = 0.0d;
	double oddsRatio = 0.0d;
	public CIvalue(double oddsratio, double upper, double lower)
	{
		setOddsRatio(oddsratio);
		setUpper(upper);
		setLower(lower);
	}

	public void setOddsRatio(double oddsratio)
	{
		this.oddsRatio = oddsratio;
	}

	public void setUpper(double upper)
	{
		this.upper = upper;
	}

	public void setLower(double lower)
	{
		this.lower = lower;
	}

	public double getOddsRatio()
	{
		return oddsRatio;
	}

	public double getUpper()
	{
		return upper;
	}

	public double getLower()
	{
		return lower;
	}
}
