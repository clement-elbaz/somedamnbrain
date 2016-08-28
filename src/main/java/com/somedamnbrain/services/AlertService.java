package com.somedamnbrain.services;

import com.somedamnbrain.entities.Entities.DiagnosticResult;

public class AlertService {

	public void alert(DiagnosticResult diag) {
		System.out.println("ALERT - " + diag.getHumanMessage());
	}

}
