package com.biogen;

public class Dummy {

	public Dummy() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		String str = "Create Database= 5fj 'ddfd '";
		String[] spl = str.split("[ =]", 1);
		
		System.out.println(spl);
	}
}
