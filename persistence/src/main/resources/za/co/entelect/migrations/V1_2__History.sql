BEGIN TRANSACTION
GO

CREATE TABLE dbo.HistoryEvent
	(
	[Id] bigint NOT NULL IDENTITY(1, 1),
	[Version] bigint NOT NULL DEFAULT 0,
	[EventType] nvarchar(30) NOT NULL,
	[Timestamp] datetime NOT NULL
	)  ON [PRIMARY]
GO

CREATE TABLE dbo.HistoryEventEntity
	(
	[Id] bigint NOT NULL PRIMARY KEY IDENTITY(1, 1),
	[Version] bigint NOT NULL DEFAULT 0,
	[EntityType] nvarchar(20) NOT NULL,
	[EntityId] int NOT NULL,
	[EntityRole] nvarchar(30) NOT NULL,
	[HistoryEvent] bigint NOT NULL
	)  ON [PRIMARY]
GO

CREATE NONCLUSTERED INDEX IX_EntityType_EntityId ON dbo.HistoryEventEntity
	(
	EntityType,
	EntityId
	) WITH( STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
GO

CREATE TABLE dbo.HistoryTemplate
	(
	[Id] int NOT NULL PRIMARY KEY IDENTITY(1, 1),
	[Version] bigint NOT NULL DEFAULT 0,
	[EventType] nvarchar(30) NOT NULL,
	[EntityRole] nvarchar(30) NOT NULL,
	[Template] nvarchar(1000) NOT NULL
	)  ON [PRIMARY]
GO

CREATE CLUSTERED INDEX idx_HistoryEvents_TimeStamp
ON dbo.HistoryEvent (Timestamp DESC);
GO

ALTER TABLE dbo.HistoryEvent
ADD CONSTRAINT PK_HistoryEvents PRIMARY KEY NONCLUSTERED ([Id] ASC);
GO

ALTER TABLE dbo.HistoryEventEntity
WITH CHECK ADD CONSTRAINT FK_HistoryEventEntity_HistoryEvent FOREIGN KEY(HistoryEvent)
REFERENCES dbo.HistoryEvent ([Id]);
GO

ALTER TABLE dbo.HistoryEventEntity CHECK CONSTRAINT FK_HistoryEventEntity_HistoryEvent;
GO

CREATE NONCLUSTERED INDEX idx_HistoryEventEntity_EntityIDEntityTypeHistoryEvent
ON dbo.HistoryEventEntity (EntityId,EntityType,HistoryEvent);
GO

INSERT [dbo].[HistoryTemplate] ([EventType], [EntityRole], [Template]) VALUES
	(N'REGISTERED_USER', N'OBJECT', N'Registered account.'),

  (N'CREATED_USER', N'OBJECT', N'Account created by {actor}.'),
  (N'CREATED_USER', N'ACTOR', N'Created account for {object}.')
GO

COMMIT TRANSACTION
