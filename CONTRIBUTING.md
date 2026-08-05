# Contribuer

## Préparer une modification

1. Créer une branche depuis `main`.
2. Garder les commits ciblés et explicites.
3. Ne jamais versionner `.env`, des identifiants ou des fichiers générés.
4. Ajouter ou adapter les tests lorsque le comportement change.

## Convention de commits

Le projet suit des préfixes proches de Conventional Commits : `feat`, `fix`, `refactor`, `test`, `docs`, `style`, `chore`.

Exemple :

```text
feat(posts): add pagination to the public feed
```

## Avant une pull request

```bash
npm --prefix apps/web run lint
npm --prefix apps/web run build
cd apps/api && ./mvnw test
docker compose config
```

