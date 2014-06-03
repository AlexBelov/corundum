def power base, p
	if (p == 1) 
		return base 
	end

	if (p == 0)
		return 1
	end

	a = power(base, p-1)*base
	return a
end

#file_name = ""
#puts "Please enter file name:"
#file_name = gets()
v = power(2,55)

puts(v)

fv = v + 0.1
puts(fv)
iv = 1
iv2 = 0

fv2 = fv + 1.0
iv2 = to_int(fv) + 1

puts(fv2)
puts(iv2)

arr = []
arr[0] = 1
arr[1] = 2.567
arr[2] = "HELLO"
puts_mas arr

if (iv2 > v)
	puts('Greater')
else
	puts('Fail')
end

# a = power(2,128) + 'Out'
# puts(a)

# mas = []
# # mas = read_file file_name
# mas = read_file "test/lab4.txt"
# mas = sort mas
# # puts_mas mas
# write_file "test/lab4_sorted.txt", mas

def puts_mas mas
  length = len(mas)
  for i = 0; i < length; i+=1
    puts mas[i]
  end
end
