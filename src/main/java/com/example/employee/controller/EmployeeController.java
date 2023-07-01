package com.example.employee.controller;

import java.awt.print.PrinterException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.example.employee.model.Employee;
import com.example.employee.service.EmployeeService;
import com.google.gson.Gson;

@RestController
@RequestMapping("/api")
public class EmployeeController {
	@Autowired
	EmployeeService empService;
	@RequestMapping(value="/printers")

	public String getPrinters() {
		PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
		String result = "";

		for (PrintService printer : printServices) {
			result += printer.getName() + '.';
		}	

		Gson gson = new Gson();
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("printers", result);
		String json = gson.toJson(response);
	    return json;
	}

	@RequestMapping(value="/print", method=RequestMethod.POST)
	public String readEmployees(@RequestBody Map<String, Object> saleInfo ) throws IOException, PrinterException {
		//Map<String, Object> orders = (Map<String, Object>) saleInfo.get("orderDetail");
		List<Map<String, Object>> orders = (List<Map<String, Object>>) saleInfo.get("orderDetail");
		System.out.println(orders.toString());
		PDFGenerator pdfGenerator = new PDFGenerator(saleInfo);
		orders.forEach( valor -> {
			Map<String, Object> item = valor;
			System.out.println(item.get("nombre"));
			System.out.println("/n");
		});
		String printerName = (String) saleInfo.get("printerName");
		pdfGenerator.createPDFKitchen(printerName);

		return "string";
	}
	
	@RequestMapping(value="/employees/{empId}", method=RequestMethod.PUT)
	public Employee updateEmployee(@PathVariable(value = "empId") Long id, @RequestBody Employee empDetails) {
	    return empService.updateEmployee(id, empDetails);
	}
	
	@RequestMapping(value="/employees/{empId}", method=RequestMethod.DELETE)
	public void deleteEmployees(@PathVariable(value = "empId") Long id) {
	    empService.deleteEmployee(id);
	}
}



