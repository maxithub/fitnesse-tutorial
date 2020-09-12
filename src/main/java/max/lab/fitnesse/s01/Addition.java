package max.lab.fitnesse.s01;

import lombok.Setter;

@Setter
public class Addition {
	private int a;
	private int b;
	
	public int result() {
		return a + b;
	}
}
