package main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import def.Logger;
import parser.Reader;
import executor.Execute;
import jxl.read.biff.BiffException;

public class Main {

	@SuppressWarnings("rawtypes")
	public static void main(String[] args) throws BiffException, IOException {
		
		HashMap readResult = new HashMap();
		
		readResult = Reader.read("1");
		
		//Logger.readHashMap((HashMap)readResult.get("tests"));
		
		HashMap<String, HashMap<String, ArrayList<String[]>>> status = Execute.performExecution((String[])readResult.get("config"), (HashMap)readResult.get("tests"), (HashMap)readResult.get("test_data"), (ArrayList)readResult.get("default_steps"));
		
		Logger.readHashMap(status);
	}

}
