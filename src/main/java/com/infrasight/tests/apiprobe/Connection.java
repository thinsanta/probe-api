package com.infrasight.tests.apiprobe;

import org.json.JSONArray;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Connection {
    public static void myGETRequest() throws IOException {



            URL requestUrl = new URL("https://api.arbetsformedlingen.se/af/v0/arbetsformedling/soklista/lan");
            String readLine = null;
            HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Accept-Language", "charset=utf-8; qs=1");


                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuffer response = new StringBuffer();

                while ((readLine = in.readLine()) != null) {
                    response.append(readLine);
                }
                in.close();


                JSONObject json = new JSONObject(response.toString());
                JSONObject jsonSoklista = json.getJSONObject("soklista");
                JSONArray jsonSokdata = jsonSoklista.getJSONArray("sokdata");
                JSONObject jsonSkaneCounty = jsonSokdata.getJSONObject(10);




                URL officeURL = new URL("https://api.arbetsformedlingen.se/af/v0/arbetsformedling/platser?lanid=12");
                String officeReadLine = null;
                HttpURLConnection officeConnection = (HttpURLConnection) officeURL.openConnection();

                officeConnection.setRequestMethod("GET");
                officeConnection.setRequestProperty("Accept", "application/json");
                officeConnection.setRequestProperty("Accept-Language","charset=utf-8; qs=1");



                    BufferedReader officeIn = new BufferedReader(new InputStreamReader(officeConnection.getInputStream()));
                    StringBuffer officeResponse = new StringBuffer();

                    while ((officeReadLine = officeIn.readLine()) != null) {
                        officeResponse.append(officeReadLine);
                    }
                    officeIn.close();

                    JSONObject jsonOffice = new JSONObject(officeResponse.toString());
                    JSONObject jsonAflista = jsonOffice.getJSONObject("arbetsformedlingslista");
                    JSONArray jsonAfplatsdata = jsonAflista.getJSONArray("arbetsformedlingplatsdata");

                    List<Office> officeList = new ArrayList<Office>();

                    for (int i = 0; i < jsonAfplatsdata.length(); i++) {
                        JSONObject jsonObject = jsonAfplatsdata.getJSONObject(i);
                        Office office = new Office(jsonObject.getString("afplatsnamn"), jsonObject.getString("afplatskod"));
                        officeList.add(office);
                    }


                    URL jobAdvertsUrl = new URL("https://api.arbetsformedlingen.se/af/v0/platsannonser/soklista/kommuner?lanid=12");

                    HttpURLConnection jobAdvertConnection = (HttpURLConnection) jobAdvertsUrl.openConnection();

                    jobAdvertConnection.setRequestMethod("GET");
                    jobAdvertConnection.setRequestProperty("Accept", "application/json");
                    jobAdvertConnection.setRequestProperty("Accept-Language","charset=utf-8; qs=1");



                        BufferedReader jobAdvertsIn = new BufferedReader(new InputStreamReader(jobAdvertConnection.getInputStream()));
                        String jobAdvertInputLine;
                        StringBuffer jobAdvertsResponse = new StringBuffer();
                        while ((jobAdvertInputLine = jobAdvertsIn.readLine()) != null) {
                            jobAdvertsResponse.append(jobAdvertInputLine);
                        }
                        jobAdvertsIn.close();


                        JSONObject jsonJobadvert = new JSONObject(jobAdvertsResponse.toString());
                        JSONObject jobAdvertSoklista = jsonJobadvert.getJSONObject("soklista");
                        JSONArray jobAdvertSokdata = jobAdvertSoklista.getJSONArray("sokdata");


                        Map<String, Integer> jobAdvertsPerCity = new HashMap<String, Integer>();


                        for (int i = 0; i < jobAdvertSokdata.length(); i++) {
                            JSONObject jsonAdvertObject = jobAdvertSokdata.getJSONObject(i);
                            String name = jsonAdvertObject.getString("namn");
                            Integer jobAdverts = jsonAdvertObject.getInt("antal_platsannonser");
                            jobAdvertsPerCity.put(name, jobAdverts);
                        }

                            County scaniaCounty = new County();
                            scaniaCounty.id = (String) jsonSkaneCounty.get("id");
                            scaniaCounty.name = (String) jsonSkaneCounty.get("namn");
                            scaniaCounty.offices = officeList;
                            scaniaCounty.numJobAdverts = (Integer) jsonSkaneCounty.get("antal_platsannonser");
                            scaniaCounty.jobAdvertsPerCity = (Map<String, Integer>) jobAdvertsPerCity;
                            System.out.println(scaniaCounty);



    }
}
