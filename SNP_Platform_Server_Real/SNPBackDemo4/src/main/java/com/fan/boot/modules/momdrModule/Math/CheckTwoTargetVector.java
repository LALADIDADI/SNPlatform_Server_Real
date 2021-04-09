package com.fan.boot.modules.momdrModule.Math;


public class CheckTwoTargetVector
{
	public  CheckTwoTargetVector()
	{

	}
	public boolean isEquare(int[] v1, int[] v2)
	{
		for( int i = 0 ; i < v1.length ; i++)
		{
			if( v1[ i ] != v2[ i ])
			{
				return false;
			}
		}
		return true;
	}
}
