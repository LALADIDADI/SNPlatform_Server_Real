package com.fan.boot.modules.momdrModule.MulitifactorDimensionalityReduction;

public class Sample
{
	private int[] elements = null;
	private boolean type = false; // ture is cases and false is controls.

	public Sample(int[] elements, boolean type)
	{
		setElements(elements);
		setType(type);
	}

	public Sample(int[] elements, int type)
	{
		setElements(elements);
		setType(type);
	}

	public Sample(){}

	public void setElements(int[] elements)
	{
		this.elements = elements.clone();
	}

	public void setType(boolean type)
	{
		this.type = type;
	}

	public void setType(int type)
	{
		if(type==0)
		{
			this.type = false;
		}
		else if(type==1)
		{
			this.type = true;
		}
		else
		{
			System.out.println("The type only allows the cases and controls.");
		}
	}

	public int[] getElements()
	{
		return elements;
	}

	public int getIndexOfElements(int index)
	{
		return elements[index];
	}

	public boolean getType()
	{
		return type;
	}
}
