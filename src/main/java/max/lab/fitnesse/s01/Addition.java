package max.lab.fitnesse.s01;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
public class Addition {
	private int a;
	private int b;
	
	public int result() {
		log.info("Calculating a+b...");
		return SuperComplexAddition.add(a, b);
	}
}
