<html>
<head>
	<title>����ҳ��</title>
</head>
<body>
	ѧ����Ϣ��<br>
	ѧ�ţ�${student.id}<br>
	������${student.name}<br>
	���䣺${student.age}<br>
	��ͥסַ��${student.address}<br>
	ѧ���б�<br>
	<table border="1">
		<tr>
			<th>���</th>
			<th>ѧ��</th>
			<th>����</th>
			<th>����</th>
			<th>��ͥסַ</th>
		</tr>
		<#list stuList as stu>
		<#if stu_index%2==0>
		<tr bgcolor="red">
		<#else>
		<tr bgcolor="blue">
		</#if>
			<td>${stu_index}</td>
			<td>${stu.id}</td>
			<td>${stu.name}</td>
			<td>${stu.age}</td>
			<td>${stu.address}</td>
		</tr>
		</#list>
	</table>
	<br>
	�������͵Ĵ���${date?string("yyyy/MM/dd HH:mm:ss")}
	<br>
	nullֵ�Ĵ���${val!}
	<br>
	ʹ��if�ж�nullֵ��
	<#if val??>
	val����ֵ�ġ�����
	<#else>
	valֵΪnull������
	</#if>
	<br>
	include��ǩ���ԣ�
	<#include "hello.ftl">
</body>
</html>