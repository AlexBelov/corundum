         5
2.4     
"abc\""
'1'
true
nil
b = 0
exp1 = 100+2*3/(3+4*3)
exp2 = 100.5+2*3.1/(3+4*3)
exp3 = "hello "*2
exp4 = a + 2*3

#comment
=begin
multiline comment
=end

a = "123"
c = a + 1 + '134sg'
100+2*34
a = b

require '123.rb'

def func1(a,b)
	a = b + 1
end

def func2 a,b
	a = b + 1
	return b
end

def func3
	return 1
end

if a<b and b<c
	"lalala"
end

if a != nil
	a = "1111"
else
	a = "2222"
end

unless 1 < 2
	return a
end

func1(a, "123")

func1 a, 'abc'

while a < b
	a = "123"
	b = 2
end

func2 a, b

a = (1+2)*3

a = []
a[1] = 4

func3 a, 'abc', func1(a, "123")

for i=0;i<10;i+=1
	return 1
end

for(i=0;i<10;i+=1)
	return 1
end
