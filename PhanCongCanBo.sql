CREATE TABLE GIAMTHI (
    idGV Nvarchar(255),
    nameGV Nvarchar(255),
    birthDay Nvarchar(255),
    DVCT Nvarchar(255),
);
CREATE TABLE PHONGTHI (
    PhongThi Nvarchar(255),
    GhiChu Nvarchar(255),
);

INSERT INTO GIAMTHI(idGV, nameGV, birthDay, DVCT) VALUES ('1.18170218E8','Nguyễn Ý','36457.0','Trường THPT Nguyễn Trãi -ĐN');
select * from GIAMTHI where nameGV = N'Nguyễn Ý';
select * from PHONGTHI;
select * from GIAMTHI;
delete from GIAMTHI;
delete from PHONGTHI;
DROP TABLE GIAMTHI;
DROP TABLE PHONGTHI;