
public class test {

	public static void main(String[] args) {
		Clock clock = new Clock();
		
		
		
		clock.setTime(75,57);
		System.out.println("Test 1");
		for(int i = 0; i < 6; i++) {
			System.out.println(clock.getTime());
			clock.timeTick();
		}
		System.out.println();
		
		System.out.println("Test 2");
		clock.onOrOff = true;
		clock.setAlarm(0,6);
		for(int i = 0; i < 6; i++) {
			System.out.println(clock.getTime());
			clock.timeTick();
		}
						
	}

}
