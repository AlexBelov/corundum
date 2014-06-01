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
