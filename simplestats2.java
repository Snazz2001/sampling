import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Vector;

import org.apache.commons.math3.*;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class simplestats2 {


/**************Value configuration,including the bin boundary, bin volume for each feature and number of bin*************************/	
//	static int[] rejectArrayAge = {1,1,1,1,1,1,1,1,1};
	final static int binNoAge = 9;
	static int[] acceptArrayAge = {1444,2538,2171,1614,1111,719,439,197,124};
	final static int[] boundaryAge = {25,30,35,40,45,50,55,60,1000};//based on the uniform distribution
	
	
	final static int binNoIncome = 12;
//	static int[] rejectArrayIncome = {1,1,1,1,1,1,1,1,1};
//	static int[] acceptArrayIncome = {1087,1041,844,1053,1031,991,1306,891,417};
//	final static int[] boundaryIncome = {5000,6500,7600,8900,10200,12000,15000,18500,1000000000};//based on the bin information from data
	static int[] acceptArrayIncome = {114,1274,2278,1925,1291,943,579,445,307,426,362,413};
	final static int[] boundaryIncome = {2800,5600,8400,11200,14000,16800,19600,22400,25200,35000,200000,1000000000};//quantile
	
	final static int binNoWScore = 13;
	static int[] acceptArrayWScore = {32,0,5,140,160,971,1602,2235,1370,1545,1270,827,200};
//	static int[] rejectArrayWScore = {1,1,1,1,1,1,1,1,1,1,1,1,1};
	final static int[] boundaryWScore = {0,300,500,550,600,650,700,750,800,850,900,950,1000};//
	
	final static int binNoEScore = 13;
	static int[] acceptArrayEScore = {62,237,365,4149,4370,969,202,3,0,0,0,0,0};
//	static int[] rejectArrayEScore = {1,1,1,1,1,1,1,1,1,1,1,1,1};
	final static int[] boundaryEScore = {0,300,500,550,600,650,700,750,800,850,900,950,1000};//
	
	
	final static int binNoFScore = 5;
	static int[] acceptArrayFScore = {9207,695,425,22,8};
//	static int[] rejectArrayFScore = {1,1,1,1,1};
	final static int[] boundaryFScore = {1,2,3,4,5};//
	
	
	final static int binNoNumDependent = 5;
	static int[] acceptArrayNumDependent = {2711,3658,2851,844,293};
	static int[] rejectArrayNumDependent = {1,1,1,1,1};
	final static int[] boundaryNumDependent = {0,1,2,3,50};
	
	
	final static int binNoIndustry = 25;
	static int[] acceptArrayIndustry = {236,294,360,311,53,347,524,595,201,857,623,239,421,385,100,1032,365,142,589,84,130,840,492,742,440};
//	static int[] rejectArrayIndustry = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
	final static String[] boundaryIndustryS = {"Accountancy","Advertising","BusinessConsultancy","CallCentreOperations","Cleaning","ComputerServices","Construction","Education","Electricity","Finance","Health","HotelsRestaurants","Insurance","LegalServices","LeisureCulture","Manufacturing","Mining","Property","PublicAdministration","Publishing","ResearchDevelopment","Retail","TelecomsInternet","Transportation","Unknown"};//
	final static int[] boundaryIndustry = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24};//
	
	
	final static int binNoMaritalStatus = 7;
	static int[] acceptArrayMaritalStatus = {153,269,514,3468,5556,281,116};
//	static int[] rejectArrayMaritalStatus = {1,1,1,1,1};
	final static String[] boundaryMaritalStatusS = {"ANTE NUPTI","COMMUNITY","DIVORCED","MARRIED","SINGLE","UNKNOWN","WIDOWED"};
	final static int[] boundaryMaritalStatus = {0,1,2,3,4,5,6};
	
	final static int binNoProvince = 10;
	static int[] acceptArrayProvince = {711,278,6301,1916,171,832,229,235,242,2652};
//	static int[] rejectArrayProvince = {1,1,1,1,1,1,1,1,1,1};
	final static String[] boundaryProvinceS = {"Eastern Cape","Free State","Gauteng","KwaZulu Natal","Limpopo","Mpumalanga","North West","Northern Cape","Others","Western Cape"};
	final static int[] boundaryProvince = {0,1,2,3,4,5,6,7,8,9};
	
	
	final String accept = "Accepted";
	final String reject = "Declined";
	final double minProportion = 0.01;//parameter for relative proportion volume;
	final double minProportionPostCode = 0.0001;//parameter for relative proportion volume regarding to PostCode;
//	final double rejectAcceptOdd = 2;//parameter to set for relative volume;

	final static String[] criteria = {"age","income","wscore","dependent","industry","fscore","escore","maritalstatus","province","postcode"};
	final static double[] weights = {0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1,0.1};
	
	public static void main(String[] args) throws UnknownHostException, IOException{
		
		simplestats2 ss = new simplestats2();
		
		String outcome = "";
		int age = 0;
		int wscore = 0;
		int escore = 0;
		int fscore = 0;
		int dependent = 0;
		int gincome = 0;

//		int postcode = 0;
		String provinceS = "";
		int province = 0;
		String industryS = "";
		int industry = 0;
		String maritalS = "";
		int marital = 0;
		int votes = 0;
		int index = 0;
		double score = 0.0;
		int total = 0;
		/*
		 * The below code is used to load the post code volume from text file into hashtable
		 */
		Hashtable<Integer,Integer> postcode_vol = new Hashtable();
		for(int i=0;i<10000;i++)
			postcode_vol.put(i, 0);
		String postcode_file = "C:\\My Projects\\Zheng - ZA Sampling Algorithm - RAD - 419\\postcodevol.csv";
		File pf = new File(postcode_file);
		FileReader pf_reader = new FileReader(pf);
		BufferedReader buf_pf_reader = new BufferedReader(pf_reader);
		String pfline = buf_pf_reader.readLine();
		pfline = buf_pf_reader.readLine();
		int postcode = 0;
		int freq = 0;
		
		while(pfline!=null)
		{
			String[] tmp = pfline.split(",");
			postcode = Integer.parseInt(tmp[0]);
			freq = Integer.parseInt(tmp[1]);
			total = total + freq;
			postcode_vol.put(postcode, freq);
			pfline = buf_pf_reader.readLine();
		}
		buf_pf_reader.close();
		pf_reader.close();
		
		/*
		 * The below code is used to load in the features of each apps into memory, in practice, it may be loaded during waona journey rather than flat text. 
		 */
		String ip_topic_file = "C:\\My Projects\\Zheng - ZA Sampling Algorithm - RAD - 419\\sampletest.csv";
	    File ip_f = new File(ip_topic_file);
	    FileReader ip_f_reader = new FileReader(ip_f);
	    BufferedReader buf_ip_reader = new BufferedReader(ip_f_reader);
	    String ipline = buf_ip_reader.readLine();
	    ipline = buf_ip_reader.readLine();
	    while(ipline!=null)
	    {
	    	score = 0;
	    	String[] tmps = ipline.split(",");
	    	
	    	gincome = Integer.parseInt(tmps[1]);//load income info
	    	industryS = tmps[2];//load industry info
	    	industryS = industryS.replaceAll("\"", "");
	    	for(int i=0;i<boundaryIndustryS.length;i++)
	    	{
	    		if(industryS.trim().equalsIgnoreCase(boundaryIndustryS[i]))
	    		{
	    			index = i;
	    			break;
	    		}
	    	}
	    	industry = boundaryIndustry[index];//convert industry info from string to integer
	    	
	    	wscore = Integer.parseInt(tmps[3]);//load wongascore into wscore
	    	escore = Integer.parseInt(tmps[4]);//load EmpiricaScore into escore
	    	fscore = Integer.parseInt(tmps[5]);//load fraudScore into fscore
	    	
	    	maritalS = tmps[6];//load marital info
	    	maritalS = maritalS.replaceAll("\"", "");
	    	for(int i=0;i<boundaryMaritalStatusS.length;i++)
	    	{
	    		if(maritalS.trim().equalsIgnoreCase(boundaryMaritalStatusS[i]))
	    		{
	    			index = i;
	    			break;
	    		}
	    	}
	    	marital = boundaryMaritalStatus[index];//convert marital info from string to integer
	    	
	    	if(isInteger(tmps[7]))
	    	{
	    		dependent = Integer.parseInt(tmps[7]);//load dependent info.
	    	}
	    	else
	    	{
	    		dependent = 0;
	    	}
	    	
	    	postcode = Integer.parseInt(tmps[8]);//load post code info.
	    	age = Integer.parseInt(tmps[9]);//load age info into age
	    	
	    	provinceS = tmps[10];//load province info
	    	provinceS = provinceS.replaceAll("\"", "");
	    	for(int i=0;i<boundaryProvinceS.length;i++)
	    	{
	    		if(provinceS.trim().equalsIgnoreCase(boundaryProvinceS[i]))
	    		{
	    			index = i;
	    			break;
	    		}
	    	}
	    	province = boundaryProvince[index];//map province info from string to integer.

	    	//the below line is to calcuate the surprise score for the app
	    	score = score + ss.getScore(age, criteria[0])+ss.getScore(gincome, criteria[1]) + ss.getScore(wscore, criteria[2]) + ss.getScore(dependent, criteria[3]) + ss.getScore(industry, criteria[4])+ss.getScore(fscore, criteria[5])+ss.getScore(escore, criteria[6])+ss.getScore(marital, criteria[7])+ss.getScore(province, criteria[8])+ss.getScoreFromTable(postcode, criteria[9], postcode_vol, total);
	    		    	
	    	System.out.println(score+","+ss.getScore(age, criteria[0])+","+ss.getScore(gincome, criteria[1])+","+ss.getScore(wscore, criteria[2])+","+ss.getScore(dependent, criteria[3])+","+ss.getScore(industry, criteria[4])+","+ss.getScore(fscore, criteria[5])+","+ss.getScore(escore, criteria[6])+","+ss.getScore(marital, criteria[7])+","+ss.getScore(province, criteria[8])+","+ss.getScoreFromTable(postcode, criteria[9], postcode_vol, total));

	    	//the below code is used to update the distribution. Ideally, we can re-counts the bin volume in database or we can keep on update it in memory if we know the outcome immediately.
//	    	outcome = tmps[11];
//	    	ss.getUpdate(age,outcome,criteria[0]);
//	    	ss.getUpdate(gincome,outcome,criteria[1]);
//	    	ss.getUpdate(wongascore,outcome,criteria[2]);
//	    	ss.getUpdate(dependent,outcome,criteria[3]);
//	    	ss.getUpdate(industry,outcome,criteria[4]);
	    	ipline = buf_ip_reader.readLine();
	    }
	    buf_ip_reader.close();
	    ip_f_reader.close();
	}
	
	public int getScore(int value,String criterion)
	{
		double score = 0;
		int index = 0;
		double max = 0;
		double weight = 0;
		int totalAgeVolume = 0;
		boolean absVolume = false;
		boolean relVolume = false;
		double binProp = 0;
		double rejectRatio = 0;
		DescriptiveStatistics acceptstats = new DescriptiveStatistics();
		DescriptiveStatistics rejectstats = new DescriptiveStatistics();
		double lowerBound = 0;
		int[] boundary = null;
		int[] acceptArray = null;
		int[] rejectArray = null;
		if(criterion.equalsIgnoreCase("age"))
		{
			boundary = boundaryAge;
			acceptArray = acceptArrayAge;
//			rejectArray = rejectArrayAge;
			weight = weights[0];
		}
		else if(criterion.equalsIgnoreCase("income"))
		{
			boundary = boundaryIncome;
			acceptArray = acceptArrayIncome;
//			rejectArray = rejectArrayIncome;
			weight = weights[1];
		}
		else if(criterion.equalsIgnoreCase("wscore"))
		{
			boundary = boundaryWScore;
			acceptArray = acceptArrayWScore;
//			rejectArray = rejectArrayWScore;
			weight = weights[2];
		}else if(criterion.equalsIgnoreCase("dependent"))
		{
			boundary = boundaryNumDependent;
			acceptArray = acceptArrayNumDependent;
//			rejectArray = rejectArrayNumDependent;
			weight = weights[3];
		}else if(criterion.equalsIgnoreCase("industry"))
		{
			boundary = boundaryIndustry;
			acceptArray = acceptArrayIndustry;
//			rejectArray = rejectArrayIndustry;
			weight = weights[4];
		}
		else if(criterion.equalsIgnoreCase("escore"))
		{
			boundary = boundaryEScore;
			acceptArray = acceptArrayEScore;
//			rejectArray = rejectArrayEScore;
			weight = weights[5];
		}
		else if(criterion.equalsIgnoreCase("fscore"))
		{
			boundary = boundaryFScore;
			acceptArray = acceptArrayFScore;
//			rejectArray = rejectArrayFScore;
			weight = weights[6];
		}
		else if(criterion.equalsIgnoreCase("maritalstatus"))
		{
			boundary = boundaryMaritalStatus;
			acceptArray = acceptArrayMaritalStatus;
//			rejectArray = rejectArrayMaritalStatus;
			weight = weights[7];
		}
		else if(criterion.equalsIgnoreCase("province"))
		{
			boundary = boundaryProvince;
			acceptArray = acceptArrayProvince;
//			rejectArray = rejectArrayProvince;
			weight = weights[8];
		}
		
		/****find the updated index*******/
		for(int i=0;i<boundary.length;i++)
		{
			if(value<=boundary[i])
			{
				index = i;
				break;
			}
		}
		for(int i=0;i<acceptArray.length;i++)
		{
			acceptstats.addValue(acceptArray[i]);
//			rejectstats.addValue(rejectArray[i]);
		}
		max = acceptstats.getMax();
		binProp = (double)acceptArray[index]/acceptstats.getSum();
//		acceptRatio = (max-acceptArray[index])/max;
				
		if(acceptArray[index]<=30||binProp<minProportion)
		{
			score = 1000*weight;
			return (int)Math.round(score);
		}
		
//		lowerBound = acceptstats.getMean() - acceptstats.getStandardDeviation();//lower bound is mean - one standard deviation --this is another option we can use to detect the outlier
//		rejectRatio = (double)rejectArray[index]/rejectstats.getSum();
		
		score = 1000*weight*(max-acceptArray[index])/max;
		return (int)Math.round(score);
	}
	
	public int getScoreFromTable(int age,String criterion,Hashtable<Integer,Integer> htable,int total)
	{
		double score = 0;
		double max = 0;
		double weight = 0.1;
		double binProp = 0;

		//find the volume in the hashatable, set value 0 if key is not found
		max = (int)Collections.max(htable.values());
		Integer vol = htable.get(age);
		vol = ((vol==null)? 0: vol);
		if(vol==0)
		{
			htable.put(age, vol);
		}
		
		//find the proportion of the bin, if absolute number is less than 10 or relative proportion is less than 
		binProp = (double)vol/(double)total;

		if(vol<=10||binProp<minProportionPostCode)
		{
			score = 1000*weight;
			return (int)Math.round(score);
		}
		
//		lowerBound = acceptstats.getMean() - acceptstats.getStandardDeviation();//lower bound is mean - one standard deviation
		
		score = 1000*weight*(max-vol)/max;
		return (int)Math.round(score);
	}

   public static boolean isInteger(String str) {
	       if (str == null) {
	               return false;
	       }
	       int length = str.length();
	       if (length == 0) {
	               return false;
	       }
	       int i = 0;
	       if (str.charAt(0) == '-') {
	               if (length == 1) {
	                       return false;
	               }
	               i = 1;
	       }
	       for (; i < length; i++) {
	               char c = str.charAt(i);
	               if (c <= '/' || c >= ':') {
	                       return false;
	               }
	       }
	       return true;
	}
	

}
