#!/bin/bash
countries=`cut -f 3 ./country.txt | tr A-Z a-z`
for code in $countries
do
  fileName=`echo $code | tr a-z A-Z`
  wget -O ./coutry-ip-list/$fileName.csv  http://www.nirsoft.net/countryip/$code.csv
done