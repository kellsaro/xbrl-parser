# XbrlParser: from XML to Json

Class library for parsing XBRL files, forked from https://github.com/marcioalexandre/XbrlParser.git
Thanks Marcio Alexandre, great work!!

I divide the XbrlParser in two modules: this class library and a
web application at https://github.com/kellsaro/xbrl-web.git

## How to use it

Having an object InputStream with de xml <document.xbrl>:

.Create a javax.xml.parsers.DocumentBuilder object:  DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

.Parse the fileInputStream: Document doc = documentBuilder.parse(fileInputStream);

.Create an object XbrlParser: XbrlParser xbrlParser = XbrlParser(doc, inputStream.getOriginalFileName(), inputStream.getSize());

.Get the JSON: String json = xbrlParser.parseToJSON();  

## Notes from the original project

Back-End is a Java REST API that converts XBRL-based financial documents from XML to Json format. Front-End is an Angular app.

How to convert a xBRL-XML report to xBRL-JSON (via file): https://youtu.be/Xr6v4jL535w;

How to convert a xBRL-XML file to xBRL-JSON (via URL): https://youtu.be/kr9j4f1-GCY; 

To access the web app: https://xbrlframework.herokuapp.com;

More details/issues are discussed on https://marcioalexandre.wordpress.com/projects/xbrl-parser/;

Output xBRL-JSON file is based on XBRL Consortium Recommendations (xbrl-json-CR-2017-05-02).
