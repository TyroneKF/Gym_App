This directory contains database migrations.

Rules:
- Migrations are append-only
- Never edit or delete applied migrations
- Each Vx__ file represents one database version
- No DROP DATABASE statements are allowed
- Schema, views, and seed data evolve via new migrations