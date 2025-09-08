import { defineConfig } from 'vite'
import preact from "@preact/preset-vite";
import path from 'path'
import { viteSingleFile } from "vite-plugin-singlefile";
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
    root: 'src/main/frontend',
    base: './',
    plugins: [
        preact(), tailwindcss(), viteSingleFile()
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
