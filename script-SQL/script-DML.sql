INSERT INTO usuario(nome, password, username) 
VALUES
	('user', '$2a$10$5SBaT9A5Ngr7NDSy8I6PeeuZpuWePHHzdK9SvK5fIM/oObWjUM5zG', 'user@email.com'),
	('admin', '$2a$10$5SBaT9A5Ngr7NDSy8I6PeeuZpuWePHHzdK9SvK5fIM/oObWjUM5zG', 'admin@email.com'),
	('user-admin', '$2a$10$5SBaT9A5Ngr7NDSy8I6PeeuZpuWePHHzdK9SvK5fIM/oObWjUM5zG', 'user-admin@email.com');
	
INSERT INTO perfis(usuario_id, perfis)
VALUES
	(1, 1),
	(2, 2),
	(3, 1),
	(3, 2);