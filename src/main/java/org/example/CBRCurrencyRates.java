package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CBRCurrencyRates {
    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        args = sc.nextLine().split(" ");

        String code = "";
        String date = "";

        for (String arg : args) {
            if (arg.startsWith("--code=")) {
                code = arg.substring(7);
            } else if (arg.startsWith("--date=")) {
                date = arg.substring(7);
            }
        }



        if (code.isEmpty() || date.isEmpty()) {
            System.out.println("Ошибка: не указаны обязательные параметры --code и --date");
            return;
        }
        if(date.length() == 10){//переформатирование из YYYY-MM-DD в DD/MM/YYYY.
            date = date.substring(8,10)+"/"+date.substring(5,7)+"/"+date.substring(0,4);
        }else{
            System.out.println("Не корректная дата, введите дату формата YYYY-MM-DD");
        }

        String apiUrl = "https://www.cbr.ru/scripts/XML_daily.asp?date_req=" + date;

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Парсинг XML-ответа
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(url.openStream());

            // Получение всех элементов Valute
            NodeList valuteList = doc.getElementsByTagName("Valute");

            for (int i = 0; i < valuteList.getLength(); i++) {
                Element valute = (Element) valuteList.item(i);
                String charCode = valute.getElementsByTagName("CharCode").item(0).getTextContent();
                if (charCode.equals(code)) {
                    String name = valute.getElementsByTagName("Name").item(0).getTextContent();
                    String value = valute.getElementsByTagName("Value").item(0).getTextContent();
                    System.out.println(code + " (" + name + "): " + value);
                    return;
                }
            }

            System.out.println("Валюта с кодом " + code + " не найдена.");

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
