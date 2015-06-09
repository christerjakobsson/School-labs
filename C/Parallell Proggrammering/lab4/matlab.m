function [] = experiment_1()
	datad = dlmread('./report/data/exp-1-mean-dynamic.txt');
	datas = dlmread('./report/data/exp-1-mean-static.txt');

	for i = 1:48
		cs(:,i) = datas(datas(:,1)==i, 3);
		cd(:,i) = datad(datad(:,1)==i, 3);
	end

	for i = 1:48
  		ms(1, i) = mean(cs(:,i));
  		//md(1, i) = mean(cd(:,i));
  		ss(1, i) = std(cs(:,i));
  		ds(1, i) = std(cd(:,i))
	end
	plot(ms);
	//errorbar(ms, ss)
end
