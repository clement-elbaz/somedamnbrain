package com.somedamnbrain;

import java.util.Scanner;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		System.out.println("Hello World!");
		Scanner sc = new Scanner(System.in);
		String i = sc.nextLine();
		System.out.println("found : " + i);

		sc.close();
	}
}
