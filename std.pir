.sub print
  .param pmc str
  $P1 = new "String"
  $P1 = str
  print $P1
.end

.sub puts
  .param pmc str
  $P1 = new "String"
  $P1 = str
  say $P1
.end

.sub gets
  .local pmc stdin
  .local string str
  stdin = getstdin
  str = stdin.'readline'()
  .return(str)
.end

.sub len
  .param pmc array
  elements $I0, array
  .return($I0)
.end

.sub to_int
  .param pmc var
  $I0 = var
  .return($I0)
.end

.sub to_float
  .param pmc var
  $N0 = var
  .return($N0)
.end

.sub to_string
  .param pmc var
  $S0 = var
  .return($S0)
.end

.loadlib 'io_ops'

.sub read_file
  .param pmc file_name
  .local pmc array
  array = new "ResizablePMCArray"
  $S0 = file_name
  $P0 = open $S0, "r"
loop_file:
  $S0 = readline $P0
  push array, $S0
  if $P0 goto loop_file
  .return(array)
.end
