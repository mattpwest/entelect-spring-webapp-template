INSERT INTO AppUser(Id, Email, Password, FirstName, LastName, Verified, Enabled, ChangeEmailEnabled, VerificationToken, PasswordResetToken) VALUES (1, 'admin@entelect.co.za', '$2a$10$o.fQqZQCrwnnXaixs1XhOes0NwlpX8uL3Q7eYe0BO1Xxa9j9G5.Cy', 'Ser', 'Admin', 1, 1, 0, NULL, NULL);
INSERT INTO Role(Id, Name, Description) VALUES (1, 'ROLE_ADMIN', 'Adminstrator');
INSERT INTO AppUserRole(AppUser, Role) VALUES (1, 1);

INSERT INTO Template(Id,Name,Subject,Body)
VALUES
(1,'forgotPassword','Password Reset'
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
'),
(2,'verification','Account Verification'
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
'),
(3,'changeEmailVerification','Account Email Change'
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
');

INSERT INTO HistoryTemplate (EventType, EntityRole, Template) VALUES
(N'REGISTERED_USER', N'OBJECT', N'Registered account.'),
(N'CREATED_USER', N'OBJECT', N'Account created by {actor}.'),
(N'CREATED_USER', N'ACTOR', N'Created account for {object}.');
