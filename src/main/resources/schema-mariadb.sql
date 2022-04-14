CREATE TABLE IF NOT EXISTS factory_data (
	serial_number varchar(255) not null,
	factory_serial_number varchar(255),
	http_user varchar(255),
	http_pass_code varchar(255) not null,
	sku varchar(255),
	oui varchar(10) not null,
	product_class varchar(100) not null,
	additional_info_json varchar(255), 
	primary key(serial_number));