a = 1.1+2.5*2
b = 1
c = 100+(100*3)
a = 1

dyn = a+4+2
dyn += 5.2 + c * (b + 1)

d = "abc"
d = "abc"+"def"
d = "abc"*3
d = 'abc'
d = 'abc'+'def'
d = 'abc'*3

mas = []
mas[1] = 5
mas[2] = "String"

a = mas[2]

if a == 1
	a = 2
	unless b == 2
		b = 3
	end
else
	a = 3
end

for(i = 0, a = 0; i<10 && a<20; i+=1, a+=1)
	a += 1
	if a == 1
		b += 2*5
		#break
	end
end

for i=0; i<10; i+=1
	c = 2
end

a = 10
while a > 0
	a -= 1
end
