import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path'
import { viteSingleFile } from "vite-plugin-singlefile";

export default defineConfig({
    root: 'src/main/frontend',
    base: './',
    plugins: [
        react({
            jsxImportSource: 'react',
            babel: {
                plugins: ['@babel/plugin-transform-react-constant-elements']
            }
        }),
        viteSingleFile()
    ],
    resolve: {
        alias: {
            '@': path.resolve(__dirname, 'src/main/frontend'),
        },
    },
    build: {
        outDir: '../../../target/classes',
        emptyOutDir: false,
        minify: 'terser',
        cssCodeSplit: false,
        assetsInlineLimit: Infinity,
        terserOptions: {
            compress: {
                drop_console: true,
                drop_debugger: true,
            }
        }
    },
    server: {
        port: 3000,
    },
})