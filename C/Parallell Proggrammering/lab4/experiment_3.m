function [] = experiment_3()
	datad = dlmread('./report/data/exp-1-mean-dynamic.txt');
	datas = dlmread('./report/data/exp-1-mean-static.txt');

	for i = 1:48
		cs(:,i) = datas(datas(:,1)==i, 3);
		cd(:,i) = datad(datad(:,1)==i, 3);
	end

	for i = 1:48
  		ms(1, i) = mean(cs(:,i));
  		md(1, i) = mean(cd(:,i));
  	
	end

	for i = 1:48
		as(1, i) = ms(1, 1) / ms(1, i);
		ad(1, i) = md(1, 1) / md(1, i);
		ss(1, i) = std(cs(:,i));
  		ds(1, i) = std(cd(:,i));
	end

	errorbar(as, ss, 'r');
	hold on
	errorbar(ad, ds, 'b');
	hold off
	xlabel('Cores','FontSize', 17)
	ylabel('Speed-up','FontSize', 17)
	legend('static', 'dynamic')
end