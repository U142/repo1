package no.ums.pas.entrypoint;

import no.ums.ws.pas.UDEPARTMENT;


public class TestAyanami
{
	public static void main(String [] args)
	{
		System.out.println(UDEPARTMENT.class.getResource(UDEPARTMENT.class.getSimpleName()+".class"));
		UDEPARTMENT dept = new UDEPARTMENT();
		dept.setLLangpk(1);
		int i = dept.getLLangpk();
		//ExecApp.main(args);
	}
}