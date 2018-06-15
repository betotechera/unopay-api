#!/usr/bin/env ruby
require 'pg'
require 'date'

conn = PG::Connection.new("dbname=unopay port=5432 host=unopay-qa.c2u10rjpdtxz.us-west-2.rds.amazonaws.com user=administrator password=LVTQB5MkBuH0" )


res = conn.exec_params("""SELECT ci.id as install_id, ci.installment_number, ci.expiration, c.begin_date, p.\"name\", c.code as c_code, * from unovation.contract_installment ci 
                        inner join unovation.contract c on ci.contract_id = c.id 
                        inner join unovation.product p on c.product_id = p.\"id\" 
                        inner join unovation.issuer i on i.id = p.issuer_id
                        inner join unovation.hirer h on h.person_id = i.person_id
                        where h.id = c.hirer_id and i.id = p.issuer_id""")
codes = res.group_by { |installment| installment['c_code'] }.collect { |key, value| key }
codes.each { |code| 

    installments = res.select { |installment| installment['c_code'].eql? code }.sort {|a,b| a['installment_number'].to_i <=> b['installment_number'].to_i }
    
    installments.each do |installment|
        expiration = DateTime.parse(installment['expiration'])
        installment_number = installment['installment_number']
        newExpiration = expiration << 1  
        puts "-------------------------------------------------------------------------------------------------- #{code}" if installment_number.eql? '1' 
        puts "#{installment['install_id']} #{installment_number} #{expiration.strftime("%d/%m/%Y")} >> #{newExpiration.strftime("%d/%m/%Y")} #{installment['name']} #{installment['c_code']}"
        
        #conn.exec_params("update unovation.contract_installment set expiration=$1 where id=$2",[newExpiration, installment['install_id']])
    end
}

conn.finish
