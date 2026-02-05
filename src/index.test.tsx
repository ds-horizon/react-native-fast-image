import { StyleSheet, Platform, NativeModules } from 'react-native'
import React from 'react'
import { render } from '@testing-library/react-native'
import FastImage from './index'

const style = StyleSheet.create({ image: { width: 44, height: 44 } })

describe('FastImage (iOS)', () => {
    beforeAll(() => {
        Platform.OS = 'ios'
        NativeModules.FastImageViewModule = {
            preload: Function.prototype,
            clearMemoryCache: Function.prototype,
            clearDiskCache: Function.prototype,
            getOriginalSize: jest.fn().mockResolvedValue({ width: 100, height: 100 }),
        }
    })

    it('renders', () => {
        const { toJSON } = render(
            <FastImage
                source={{
                    uri: 'https://facebook.github.io/react/img/logo_og.png',
                    headers: {
                        token: 'someToken',
                    },
                    priority: FastImage.priority.high,
                }}
                style={style.image}
            />,
        )
        expect(toJSON()).toMatchSnapshot()
    })

    it('renders a normal Image when not passed a uri', () => {
        const { toJSON } = render(
            <FastImage
                source={require('../ReactNativeFastImageExampleServer/pictures/jellyfish.gif')}
                style={style.image}
            />,
        )
        expect(toJSON()).toMatchSnapshot()
    })

    it('renders Image with fallback prop', () => {
        const { toJSON } = render(
            <FastImage
                source={require('../ReactNativeFastImageExampleServer/pictures/jellyfish.gif')}
                style={style.image}
                fallback
            />,
        )
        expect(toJSON()).toMatchSnapshot()
    })

    it('renders defaultSource', () => {
        const { toJSON } = render(
            <FastImage
                defaultSource={require('../ReactNativeFastImageExampleServer/pictures/jellyfish.gif')}
                style={style.image}
            />,
        )
        expect(toJSON()).toMatchSnapshot()
    })
    it('getSize retrieves image dimensions', async () => {
        const success = jest.fn()
        FastImage.getSize('https://example.com/image.png', success)
        await new Promise(process.nextTick)
        expect(
            NativeModules.FastImageViewModule.getOriginalSize,
        ).toHaveBeenCalledWith({ uri: 'https://example.com/image.png' }, {})
        expect(success).toHaveBeenCalledWith(100, 100)
    })

    it('getSizeWithHeaders retrieves image dimensions with headers', async () => {
        const success = jest.fn()
        const headers = { Authorization: 'Bearer token' }
        FastImage.getSizeWithHeaders(
            'https://example.com/image.png',
            headers,
            success,
        )
        await new Promise(process.nextTick)
        expect(
            NativeModules.FastImageViewModule.getOriginalSize,
        ).toHaveBeenCalledWith(
            { uri: 'https://example.com/image.png', headers },
            {},
        )
        expect(success).toHaveBeenCalledWith(100, 100)
    })
})

describe('FastImage (Android)', () => {
    beforeAll(() => {
        Platform.OS = 'android'
    })

    it('renders a normal defaultSource', () => {
        const { toJSON } = render(
            <FastImage
                defaultSource={require('../ReactNativeFastImageExampleServer/pictures/jellyfish.gif')}
                style={style.image}
            />,
        )
        expect(toJSON()).toMatchSnapshot()
    })

    it('renders a normal defaultSource when fails to load source', () => {
        const { toJSON } = render(
            <FastImage
                defaultSource={require('../ReactNativeFastImageExampleServer/pictures/jellyfish.gif')}
                source={{
                    uri: 'https://www.google.com/image_does_not_exist.png',
                }}
                style={style.image}
            />,
        )
        expect(toJSON()).toMatchSnapshot()
    })

    it('renders a non-existing defaultSource', () => {
        const { toJSON } = render(
            <FastImage defaultSource={12345} style={style.image} />,
        )
        expect(toJSON()).toMatchSnapshot()
    })
})
