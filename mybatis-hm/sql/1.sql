create database mybatis;
use mybatis;

drop table if exists tb_user;

create table tb_user(
    id int primary key auto_increment,
    username varchar(20),
    password varchar(20),
    gender char(1),
    addr varchar(30)
);