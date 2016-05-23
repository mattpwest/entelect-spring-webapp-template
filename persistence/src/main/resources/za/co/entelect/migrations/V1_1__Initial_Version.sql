BEGIN TRANSACTION
GO

/** VerificationToken **/
CREATE TABLE [dbo].[VerificationToken]
(
	[Id] bigint NOT NULL IDENTITY (1, 1),
  [Version] bigint NOT NULL DEFAULT 0,
	[Token] nchar(32) NOT NULL,
	[ExpiresAt] datetime NOT NULL
)  ON [PRIMARY]
GO

ALTER TABLE [dbo].[VerificationToken] ADD CONSTRAINT
	[PK_VerificationToken] PRIMARY KEY CLUSTERED
	(
		[Id]
	) WITH( STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]

GO

CREATE UNIQUE NONCLUSTERED INDEX [IX_VerificationToken] ON [dbo].[VerificationToken]
(
	Token
) WITH( STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO

/** PasswordResetToken **/
CREATE TABLE dbo.PasswordResetToken
(
	[Id] bigint NOT NULL IDENTITY (1, 1),
  [Version] bigint NOT NULL DEFAULT 0,
	[Token] nchar(32) NOT NULL,
	[ExpiresAt] datetime NOT NULL
)  ON [PRIMARY]
GO

ALTER TABLE dbo.PasswordResetToken ADD CONSTRAINT
	PK_PasswordResetToken PRIMARY KEY CLUSTERED
	(
		[Id]
	) WITH( STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO

/** ChangeEmailVerificationToken **/
CREATE TABLE [dbo].[ChangeEmailVerificationToken]
(
  [Id] int NOT NULL IDENTITY (1, 1),
  [Version] bigint NOT NULL DEFAULT 0,
  [Token] nchar(32) NOT NULL,
  [ExpiresAt] datetime NOT NULL,
  [Used] bit NOT NULL,
  [OldEmail] nvarchar(100) NOT NULL,
  [NewEmail] nvarchar(100) NOT NULL,
  [AppUser] int NOT NULL
)
GO

ALTER TABLE [dbo].[ChangeEmailVerificationToken] ADD CONSTRAINT
  PK_ChangeEmailVerificationToken PRIMARY KEY CLUSTERED
  (
    [Id]
  ) WITH( STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO

/** AppUser **/
CREATE TABLE [dbo].[AppUser](
	[Id] [bigint] IDENTITY(1,1) NOT NULL,
  [Version] [bigint] NOT NULL DEFAULT 0,
	[Email] [nvarchar](100) NOT NULL,
	[Password] [nvarchar](100) NOT NULL,
	[FirstName] [nvarchar](100) NOT NULL,
	[LastName] [nvarchar](100) NOT NULL,
	[Verified] [bit] NOT NULL,
	[Enabled] [bit] NOT NULL,
	[ChangeEmailEnabled] [bit] NOT NULL,
	[VerificationToken] [bigint] NULL,
	[PasswordResetToken] [bigint] NULL
 CONSTRAINT [PK_AppUser] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

ALTER TABLE [dbo].[AppUser] ADD CONSTRAINT
	[FK_AppUser_VerificationToken] FOREIGN KEY
	(
		[VerificationToken]
	) REFERENCES [dbo].[VerificationToken]
	(
		[Id]
	) ON UPDATE  NO ACTION
	ON DELETE  NO ACTION
GO

ALTER TABLE [dbo].[AppUser] ADD CONSTRAINT
	[FK_AppUser_PasswordResetToken] FOREIGN KEY
	(
		[PasswordResetToken]
	) REFERENCES [dbo].[PasswordResetToken]
	(
		[Id]
	) ON UPDATE  NO ACTION
	ON DELETE  NO ACTION
GO

/** AppUserRole **/
CREATE TABLE [dbo].[AppUserRole](
	[AppUser] [bigint] NOT NULL,
	[Role] [bigint] NOT NULL,
 CONSTRAINT [PK_AppUserRole] PRIMARY KEY CLUSTERED
(
	[AppUser] ASC,
  [Role] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

/** Email **/
CREATE TABLE dbo.Email
(
	[Id] bigint NOT NULL IDENTITY (1, 1),
  [Version] bigint NOT NULL DEFAULT 0,
	Subject nvarchar(100) NOT NULL,
	Template nvarchar(100) NOT NULL,
	DataJson ntext NOT NULL,
	SentAt datetime NULL,
	AppUser bigint NOT NULL,
  Email nvarchar(100) NOT NULL,
  Priority nvarchar(10) NOT NULl
)  ON [PRIMARY]
	TEXTIMAGE_ON [PRIMARY]
GO

ALTER TABLE dbo.Email ADD CONSTRAINT
	PK_Email PRIMARY KEY CLUSTERED
	(
		[Id]
	) WITH( STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]

GO

CREATE NONCLUSTERED INDEX IX_Email ON dbo.Email
(
	SentAt
) WITH( STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO

ALTER TABLE dbo.Email ADD CONSTRAINT
	FK_Email_AppUser FOREIGN KEY
	(
		AppUser
	) REFERENCES dbo.AppUser
	(
		[Id]
	) ON UPDATE  NO ACTION
	ON DELETE  NO ACTION
GO

/** Role **/
CREATE TABLE [dbo].[Role](
	[Id] [bigint] IDENTITY(1,1) NOT NULL,
  [Version] bigint NOT NULL DEFAULT 0,
	[Name] [nvarchar](50) NOT NULL,
	[Description] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_Role] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

ALTER TABLE [dbo].[AppUserRole]  WITH CHECK ADD  CONSTRAINT [FK_AppUserRole_AppUser] FOREIGN KEY([AppUser])
REFERENCES [dbo].[AppUser] ([Id])
GO

ALTER TABLE [dbo].[AppUserRole] CHECK CONSTRAINT [FK_AppUserRole_AppUser]
GO

ALTER TABLE [dbo].[AppUserRole]  WITH CHECK ADD  CONSTRAINT [FK_AppUserRole_Role1] FOREIGN KEY([Role])
REFERENCES [dbo].[Role] ([Id])
GO

ALTER TABLE [dbo].[AppUserRole] CHECK CONSTRAINT [FK_AppUserRole_Role1]
GO

/** Template for email **/
CREATE TABLE [dbo].[Template](
  [Id] [bigint] IDENTITY(1,1) NOT NULL,
  [Version] bigint NOT NULL DEFAULT 0,
  [Name] [varchar](50) NOT NULL,
  [Subject] [varchar](100) NOT NULL,
  [Body] [varchar](max) NULL,
  CONSTRAINT [PK_Template] PRIMARY KEY CLUSTERED
    (
      [Id] ASC
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

/** Insert some basic test data */
SET IDENTITY_INSERT [dbo].[AppUser] ON
GO
INSERT [dbo].[AppUser] ([Id], [Email], [Password], [FirstName], [LastName], [Verified], [Enabled], [ChangeEmailEnabled],
                        [VerificationToken], [PasswordResetToken]) VALUES
	(1, N'admin@entelect.co.za', N'$2a$10$jbaCkw5VOPudNXV7QtXU5OI2hFxJYC9I0T5VgNDEaK4jxLAGC8c0C', N'Ser', N'Admin', 1, 1, 0, NULL, NULL)
GO
SET IDENTITY_INSERT [dbo].[AppUser] OFF
GO

SET IDENTITY_INSERT [dbo].[Role] ON
GO
INSERT [dbo].[Role] ([Id], [Name], [Description]) VALUES (1, N'ROLE_ADMIN', N'Administrator')
GO
SET IDENTITY_INSERT [dbo].[Role] OFF
GO

INSERT [dbo].[AppUserRole] ([AppUser], [Role]) VALUES (1, 1)
GO

SET IDENTITY_INSERT [dbo].[Template] ON
GO
INSERT INTO [dbo].[Template] ([Id],[Name],[Subject],[Body])
VALUES (1,'forgotPassword','Password Reset'
  ,'<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">

<head>
    <title th:remove="all">Test template for HTML email with inline images</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <style>
        .red {
            color: #ff0000;
        }

        img.logo {
            float: left;
        }
    </style>
</head>

<body>
<p th:text="|Hey ${name},|">
    Hey Matt,
</p>

<p>
    To reset your password simply follow the link below and enter your new password:
    <a href="#" th:href="|${uri}?token=${token}|" th:text="|${uri}?token=${token}|">tokenUrl</a>.
</p>

<p>
    If you did not request a password reset you may simply ignore this e-mail.
</p>

<p>
</p>

<div>
    <img class="logo" src="../images/entelect-logo.jpg" th:src="|cid:${image0}|" />
    <p>
        Regards, <br />
        <em>Entelect</em>
    </p>
</div>
</body>
</html>
')
GO

INSERT INTO [dbo].[Template] ([Id],[Name],[Subject],[Body])
VALUES (2,'verification','Account Verification'
  ,'<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">

<head>
    <title th:remove="all">Test template for HTML email with inline images</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <style>
        .red {
            color: #ff0000;
        }

        img.logo {
            float: left;
        }
    </style>
</head>

<body>
<p th:text="|Hey ${name},|">
    Hey Matt,
</p>

<p>
    To verify your account please open this link in your browser:
    <a href="#" th:href="|${uri}?token=${token}|" th:text="|${uri}?token=${token}|">tokenUrl</a>.
</p>
<p>

</p>
<div>
    <img class="logo" src="../images/entelect-logo.jpg" th:src="|cid:${image0}|" />
    <p>
        Regards, <br />
        <em>Entelect</em>
    </p>
</div>
</body>
</html>
')
GO
INSERT INTO [dbo].[Template] (Id, Name, Subject, Body)
VALUES (3,'changeEmailVerification','Account Email Change'
  ,'<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">

<head>
    <title th:remove="all">Test template for HTML email with inline images</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <style>
        .red {
            color: #ff0000;
        }

        img.logo {
            float: left;
        }
    </style>
</head>

<body>
<p th:text="|Hey ${name},|">
    Hey Matt,
</p>

<p>
    There was a request to change your email address to this mail account. <br/>
    Before we can change your details, we need to make sure you have access to this account. <br/>
    Please use the link provided to verify your email address.
    <a href="#" th:href="|${uri}?token=${token}|" th:text="|${uri}?token=${token}|">tokenUrl</a>.
</p>
<p>

</p>
<div>
    <img class="logo" src="../images/entelect-logo.jpg" th:src="|cid:${image0}|" />
    <p>
        Regards, <br />
        <em>Entelect</em>
    </p>
</div>
</body>
</html>
'),
  (4,'cancelChangeEmailVerification','Account Email Change'
    ,'<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">

<head>
    <title th:remove="all">Test template for HTML email with inline images</title>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

    <style>
        .red {
            color: #ff0000;
        }

        img.logo {
            float: left;
        }
    </style>
</head>

<body>
<p th:text="|Hey ${name},|">
    Hey Matt,
</p>

<p>
    There was a request to change your email address to this mail account. <br/>
    If you did not make this request please use the link provided to cancel this request.
    <a href="#" th:href="|${uri}?token=${token}|" th:text="|${uri}?token=${token}|">tokenUrl</a>.
</p>
<p>

</p>
<div>
    <img class="logo" src="../images/entelect-logo.jpg" th:src="|cid:${image0}|" />
    <p>
        Regards, <br />
        <em>Entelect</em>
    </p>
</div>
</body>
</html>
')

SET IDENTITY_INSERT [dbo].[Template] OFF
GO

COMMIT
