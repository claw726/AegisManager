CREATE TABLE users
(UserID INTEGER PRIMARY KEY NOT NULL,
UserName TEXT NO NULL,
Email TEXT NOT NULL,
OrgIDTableName TEXT NOT NULL,
PWHash TEXT NOT NULL,
TwoFactorAuthInfo TEXT NOT NULL,
PasswordResetToken TEXT NOT NULL,
IsLoggedIn INTEGER NOT NULL);

INSERT INTO users
(UserID, UserName, Email, OrgIDTableName, PWHash,
TwoFactorAuthInfo, PasswordResetToken, IsLoggedIn)
VALUES
(1, 'mlenkeit', 'mlenkeit4@gmail.com', 'mlenkeit_1_OrgTable',
'Placeholder Hash', 'Placeholder 2FA', 'PlaceholderToken', 0);

SELECT Email FROM users