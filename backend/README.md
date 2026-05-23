# BoxVista Backend

## Supabase PostgreSQL setup

1. Copy `.env.example` to `.env`.
2. Fill `DATABASE_URL` with the Supabase Postgres URI, or use `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, and `DB_PASSWORD`.
3. Create the database tables:

```sh
npm run db:init
```

This creates the `users`, `boxes`, and `objects` tables from the Sequelize models. Boxes belong to users through `boxes.userId`, and objects belong to boxes through `objects.boxId`. Use `npm run db:reset` only when you intentionally want to drop and recreate those tables.

For an existing Supabase database, add the user relationship without deleting existing data:

```sql
alter table public.boxes add column if not exists "userId" integer;

do $$
begin
  if not exists (
    select 1
    from pg_constraint
    where conname = 'boxes_userId_fkey'
  ) then
    alter table public.boxes
      add constraint "boxes_userId_fkey"
      foreign key ("userId")
      references public.users(id)
      on delete cascade;
  end if;
end $$;

create index if not exists "boxes_userId_idx" on public.boxes ("userId");
```

Existing boxes keep `userId = null` until you backfill them to an existing user. New boxes created through the API always receive the authenticated user's id.

To migrate the existing local SQLite data into Supabase:

```sh
npm run db:migrate:sqlite
```

This reads `database.sqlite`, preserves the existing `id` values, and upserts `boxes` and `objects`. Because boxes now belong to a user, set `MIGRATION_OWNER_EMAIL` to an account that already exists in `users` before importing:

```sh
MIGRATION_OWNER_EMAIL=user@example.com npm run db:migrate:sqlite
```

To delete existing Supabase boxes/objects before importing, run:

```sh
MIGRATION_OWNER_EMAIL=user@example.com npm run db:migrate:sqlite -- --replace
```

Run the API with:

```sh
npm run dev
```
