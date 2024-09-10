INSERT INTO usuario(nome, password, username) 
VALUES
	('user', '$2a$10$RLnExQvs2J0syC.DrAmVieOTOjI.1MkSI2er0mLi/7Vmn4W4lddsK', 'user@email.com'),--Senha01@.
	('admin', '$2a$10$RLnExQvs2J0syC.DrAmVieOTOjI.1MkSI2er0mLi/7Vmn4W4lddsK', 'admin@email.com'),
	('user-admin', '$2a$10$RLnExQvs2J0syC.DrAmVieOTOjI.1MkSI2er0mLi/7Vmn4W4lddsK', 'user-admin@email.com');
	
INSERT INTO perfis(usuario_id, perfis)
VALUES
	(1, 1),
	(2, 2),
	(3, 1),
	(3, 2);