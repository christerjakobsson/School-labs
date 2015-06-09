function [] = experiment_2(input)
	data = dlmread(input);

	for i = 1:48
		reps = data(data(:,1)==i, 2:3);
		idleTime = 0;
		
		for j = 1:10
			rep = reps(reps(:,1)==j, 2);
			maxTime = max(rep);
			idleTime(j,:) = sum(maxTime - rep);
		end	

		total(i, 1) = mean(idleTime);
		stdTot(i, 1) = std(idleTime);	
	end

	errorbar(total, stdTot)
	xlabel('Cores','FontSize', 17)
	ylabel('Mean idle time','FontSize', 17)
end