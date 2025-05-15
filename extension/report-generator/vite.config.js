import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'

export default defineConfig({
    root: 'src/main/frontend',
    base: './',
    plugins: [react()],
    resolve: {
        alias: {
            '@': path.resolve(__dirname, 'src/main/frontend'),
        },
    },
    build: {
        outDir: '../../../target/classes',
        emptyOutDir: false,
    },
    server: {
        port: 3000,
    },
})