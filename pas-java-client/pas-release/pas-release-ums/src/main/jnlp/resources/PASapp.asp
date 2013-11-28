<%@ Language=VBScript %>
<%	Option Explicit
%>
<%

'href=""PASapp.jnlp""
Response.clear()
Response.ContentType = "application/x-java-jnlp-file"
'Response.ContentType = "text/plain"
Dim i
Dim key
Dim fs
Dim currentDir
Dim szJnlpFile
Dim szContent
Dim szTempContent
Dim fJnlp
Dim strToFind
dim nstart
dim nend
set fs = Server.CreateObject("Scripting.FileSystemObject")
currentDir = Server.MapPath(".")
szJnlpFile = currentDir & "/pas.jnlp"

if fs.FileExists(szJnlpFile) then
    'Response.Write("ok")
    set fJnlp = fs.OpenTextFile(szJnlpFile,1)
    szContent = fjnlp.ReadAll()
    fJnlp.Close()

    'find and remove shortcut tag // removed due to signing issues
    'strToFind = "<shortcut"
    'nstart = InStr(1, szContent, strToFind)
    'strToFind = "</shortcut>"
    'nend = InStr(1, szContent, strToFind)
    'szTempContent = left(szContent, nstart-1)
    'szTempContent = szTempContent & mid(szContent, nend+len(strToFind))
    'szContent = szTempContent

    'find and remove offline tag // removed due to signing issues
    'strToFind = "<offline-allowed/>"
    'nstart = InStr(1, szContent, strToFind)
    'szTempContent = left(szContent, nstart-1)
    'szTempContent = szTempContent & mid(szContent, nstart+len(strToFind))
    'szContent = szTempContent

    'find arguments
    strToFind = "</argument>"
    nstart = InStrRev(szContent, strToFind)
    szTempContent = left(szContent, nstart-1)
    for each key in Request.Querystring
    	szTempContent = szTempContent & ";-" & key & Request.QueryString(key)
    next
    szContent = szTempContent & mid(szContent, nstart)

    Response.Write(szContent)
else
    Response.Write("File not found: " & szJnlpFile)
end if

Response.Flush()
Response.End()

%>